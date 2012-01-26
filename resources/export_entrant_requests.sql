--
-- Copyright (c) 2008-2011, Kir-Dev
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions are met:
-- * Redistributions of source code must retain the above copyright
-- notice, this list of conditions and the following disclaimer.
--  * Redistributions in binary form must reproduce the above copyright
-- notice, this list of conditions and the following disclaimer in the
-- documentation and/or other materials provided with the distribution.
--  * Neither the name of the Peter Major nor the
-- names of its contributors may be used to endorse or promote products
-- derived from this software without specific prior written permission.
--  * All advertising materials mentioning features or use of this software
-- must display the following acknowledgement:
-- This product includes software developed by the Kir-Dev Team, Hungary
-- and its contributors.
--
-- THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
-- EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
-- WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
-- DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
-- DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
-- (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
-- LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
-- ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
-- (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
-- SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--


DROP TYPE IF EXISTS exported_entrant_request CASCADE;

--
-- Name: exported_entrant_request; Type: TYPE; Schema: public; Owner: kir
--

CREATE TYPE exported_entrant_request AS (
	uid integer,
        nev text,
        neptun character(6),
        email text,
        primary_group text,
        entrant_num bigint,
        indokok text
);

ALTER TYPE public.exported_entrant_request OWNER TO kir;

CREATE OR REPLACE FUNCTION export_entrant_requests(text, text, integer) RETURNS SETOF exported_entrant_request
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

ALTER FUNCTION public.export_entrant_requests(text, text, integer) OWNER TO kir;
