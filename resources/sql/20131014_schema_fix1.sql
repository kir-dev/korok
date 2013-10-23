alter table ertekeles_uzenet ALTER column group_id TYPE bigint;
alter table ertekeles_uzenet ALTER column semester DROP DEFAULT;
alter table ertekeles_uzenet ALTER column semester TYPE varchar(9);

alter table ertekelesek ALTER column semester TYPE varchar(9);

alter table groups ALTER column grp_id TYPE bigint;
alter table groups ALTER column grp_state TYPE varchar;
alter table groups ALTER column grp_parent TYPE bigint;

alter table grp_membership ALTER column id TYPE bigint;
alter table grp_membership ALTER column grp_id TYPE bigint;
alter table grp_membership ALTER column usr_id TYPE bigint;

alter table im_accounts ALTER column usr_id TYPE bigint;

alter table log ALTER column id TYPE bigint;
alter table log ALTER column grp_id TYPE bigint;
alter table log ALTER column usr_id TYPE bigint;

alter table poszt ALTER column id TYPE bigint;
alter table poszt ALTER column grp_member_id TYPE bigint;
alter table poszt ALTER column pttip_id TYPE bigint;

alter table poszttipus ALTER column pttip_id TYPE bigint;
alter table poszttipus ALTER column grp_id TYPE bigint;

alter table spot_images ALTER column usr_neptun TYPE varchar;

alter table users ALTER column usr_password TYPE varchar(28);
alter table users ALTER column usr_salt TYPE varchar(12);
alter table users ALTER column usr_neptun TYPE varchar;
alter table users ALTER column usr_confirm TYPE varchar(64);
alter table users ALTER column usr_est_grad TYPE varchar(10);
alter table users ALTER column usr_svie_primary_membership TYPE bigint;

-- just replace the type of uid from integer to bigint
-- we have to drop the type and the function because of the dependencies
DROP FUNCTION export_entrant_requests(text, text, integer);
DROP TYPE exported_entrant_request;
CREATE TYPE exported_entrant_request AS (
  uid bigint,
  nev text,
  neptun varchar,
  email text,
  primary_group text,
  entrant_num bigint,
  indokok text
);

-- nothing changed, really
CREATE FUNCTION export_entrant_requests(text, text, integer) RETURNS SETOF exported_entrant_request
    LANGUAGE sql
    AS $_$
SELECT
    u.usr_id, u.usr_lastname || ' ' || u.usr_firstname as nev, u.usr_neptun as neptun, u.usr_email as email,
    primary_g.grp_name as primary_group, COUNT(*) as entrant_num,
    array_to_string(array_agg('*' || g.grp_name || '*: ' || bi.szoveges_ertekeles), ' || ') as indokok
  FROM belepoigenyles bi
  INNER JOIN ertekelesek e ON bi.ertekeles_id = e.id
  INNER JOIN users u ON bi.usr_id = u.usr_id
  INNER JOIN groups g ON e.grp_id = g.grp_id
  LEFT JOIN grp_membership gm ON u.usr_svie_primary_membership = gm.id
  LEFT JOIN groups primary_g ON gm.grp_id = primary_g.grp_id

  WHERE bi.belepo_tipus = $2 AND
  e.semester = $1 AND
  e.belepoigeny_statusz = 'ELFOGADVA' AND
  e.next_version IS NULL
  GROUP BY u.usr_id, u.usr_lastname, u.usr_firstname, u.usr_neptun, u.usr_email, primary_g.grp_name
  HAVING COUNT(*) >= $3
  ORDER BY nev;
$_$;

drop FUNCTION kirauth ( text, text) ;
drop FUNCTION rndchar ( ) ;
