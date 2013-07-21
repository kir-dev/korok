--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: exported_entrant_request; Type: TYPE; Schema: public; Owner: -
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


--
-- Name: user_points; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE user_points AS (
	neptun character(6),
	points numeric
);


--
-- Name: delete_oids(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION delete_oids() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  PERFORM lo_unlink(OLD.image);
  RETURN OLD;
END
$$;


--
-- Name: export_entrant_requests(text, text, integer); Type: FUNCTION; Schema: public; Owner: -
--

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


--
-- Name: getpointsforsemester(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION getpointsforsemester(text, text) RETURNS SETOF user_points
    LANGUAGE sql
    AS $_$
SELECT UPPER(users.usr_neptun) AS neptun,
LEAST(vegso.atlag,100) FROM
(SELECT p.user_id AS usr_id, TRUNC(SQRT(SUM(p.sum * p.sum))) AS atlag FROM
(SELECT pontigenyles.usr_id AS user_id, v.grp_id, SUM(pontigenyles.pont) AS sum FROM ertekelesek v
RIGHT JOIN pontigenyles ON pontigenyles.ertekeles_id = v.id
WHERE
  v.next_version IS NULL -- az elfogadottak közül csak a legfrisebbet nézzük
  AND v.pontigeny_statusz = 'ELFOGADVA' -- legyen elfogadva
  AND (v.semester = $1 OR v.semester = $2) -- jelenlegi és az előző félév
GROUP BY v.grp_id, pontigenyles.usr_id) AS p
GROUP BY p.user_id) AS vegso
INNER JOIN users ON users.usr_id = vegso.usr_id AND users.usr_neptun IS NOT NULL
ORDER BY neptun ASC
$_$;


--
-- Name: kirauth(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION kirauth(text, text) RETURNS text
    LANGUAGE sql
    AS $_$SELECT usr_id || '/0/0' FROM users WHERE (lower($1) IN (lower(usr_email), lower(usr_neptun)) OR (lower($1) || '@sch.bme.hu' IN (lower(usr_email )))) AND usr_passwd = $2;$_$;


--
-- Name: rndchar(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION rndchar() RETURNS text
    LANGUAGE sql
    AS $$SELECT chr(96+ceil(random()*26)::integer)$$;


--
-- Name: update_user_recommended_photo_after_insert(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION update_user_recommended_photo_after_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
BEGIN
  UPDATE users SET usr_show_recommended_photo = TRUE WHERE usr_neptun = NEW.usr_neptun;
  RETURN NULL;
END;
$$;


--
-- Name: update_user_recommended_photo_before_delete(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION update_user_recommended_photo_before_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
BEGIN
  UPDATE users SET usr_show_recommended_photo = FALSE WHERE usr_neptun = OLD.usr_neptun;
  RETURN OLD;
END;
$$;


SET default_tablespace = '';

SET default_with_oids = true;

--
-- Name: belepoigenyles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE belepoigenyles (
    id bigint NOT NULL,
    belepo_tipus character varying(255),
    szoveges_ertekeles text,
    ertekeles_id bigint NOT NULL,
    usr_id bigint
);


--
-- Name: ertekeles_uzenet; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE ertekeles_uzenet (
    id bigint NOT NULL,
    feladas_ido timestamp without time zone,
    uzenet text,
    felado_usr_id bigint,
    group_id integer,
    semester character(9) DEFAULT ''::bpchar NOT NULL,
    from_system boolean DEFAULT false
);


--
-- Name: ertekelesek; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE ertekelesek (
    id bigint NOT NULL,
    belepoigeny_statusz character varying(255),
    feladas timestamp without time zone,
    pontigeny_statusz character varying(255),
    semester character(9) NOT NULL,
    szoveges_ertekeles text NOT NULL,
    utolso_elbiralas timestamp without time zone,
    utolso_modositas timestamp without time zone,
    elbiralo_usr_id bigint,
    grp_id bigint,
    felado_usr_id bigint,
    pontozasi_elvek text DEFAULT ''::text NOT NULL,
    next_version bigint,
    explanation text,
    optlock integer DEFAULT 0 NOT NULL,
    is_considered boolean DEFAULT false NOT NULL
);


--
-- Name: event_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE event_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_with_oids = false;

--
-- Name: event; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE event (
    evt_id integer DEFAULT nextval('event_seq'::regclass) NOT NULL,
    evt_text character varying(30)
);


--
-- Name: groups_grp_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE groups_grp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_with_oids = true;

--
-- Name: groups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE groups (
    grp_id integer DEFAULT nextval('groups_grp_id_seq'::regclass) NOT NULL,
    grp_name text NOT NULL,
    grp_type character varying(20) NOT NULL,
    grp_parent integer,
    grp_state character(3) DEFAULT 'akt'::bpchar,
    grp_description text,
    grp_webpage character varying(64),
    grp_maillist character varying(64),
    grp_head character varying(48),
    grp_founded integer,
    grp_issvie boolean DEFAULT false NOT NULL,
    grp_svie_delegate_nr integer,
    grp_users_can_apply boolean DEFAULT true NOT NULL
);


--
-- Name: grp_members_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE grp_members_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_with_oids = false;

--
-- Name: grp_membership; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE grp_membership (
    id integer DEFAULT nextval('grp_members_seq'::regclass) NOT NULL,
    grp_id integer,
    usr_id integer,
    membership_start date DEFAULT now(),
    membership_end date
);


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: im_accounts_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE im_accounts_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: im_accounts; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE im_accounts (
    id bigint DEFAULT nextval('im_accounts_seq'::regclass) NOT NULL,
    protocol character varying(50) NOT NULL,
    screen_name character varying(255) NOT NULL,
    usr_id integer
);


--
-- Name: log_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: log; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE log (
    id integer DEFAULT nextval('log_seq'::regclass) NOT NULL,
    grp_id integer,
    usr_id integer NOT NULL,
    evt_id integer NOT NULL,
    evt_date date DEFAULT now()
);


--
-- Name: neptun_list; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE neptun_list (
    nev character varying(128) NOT NULL,
    neptun character varying(6) NOT NULL,
    szuldat date NOT NULL,
    education_id character varying(11) DEFAULT NULL,
    newbie BOOLEAN DEFAULT FALSE
);


SET default_with_oids = true;

--
-- Name: pontigenyles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pontigenyles (
    id bigint NOT NULL,
    pont integer,
    ertekeles_id bigint NOT NULL,
    usr_id bigint
);


--
-- Name: poszt_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE poszt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_with_oids = false;

--
-- Name: poszt; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE poszt (
    id integer DEFAULT nextval('poszt_seq'::regclass) NOT NULL,
    grp_member_id integer,
    pttip_id integer
);


--
-- Name: poszttipus_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE poszttipus_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: poszttipus; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE poszttipus (
    pttip_id integer DEFAULT nextval('poszttipus_seq'::regclass) NOT NULL,
    grp_id integer,
    pttip_name character varying(30) NOT NULL,
    delegated_post boolean DEFAULT false
);


--
-- Name: spot_images; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE spot_images (
    usr_neptun character(6) NOT NULL,
    image_path character varying(255) NOT NULL
);


SET default_with_oids = true;

--
-- Name: system_attrs; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE system_attrs (
    attributeid bigint NOT NULL,
    attributename character varying(255) NOT NULL,
    attributevalue character varying(255) NOT NULL
);


--
-- Name: users_usr_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE users_usr_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE users (
    usr_id bigint DEFAULT nextval('users_usr_id_seq'::regclass) NOT NULL,
    usr_email character varying(64),
    usr_neptun character(6),
    usr_firstname text NOT NULL,
    usr_lastname text NOT NULL,
    usr_nickname text,
    usr_svie_state character varying(255) DEFAULT 'NEMTAG'::character varying NOT NULL,
    usr_svie_member_type character varying(255) DEFAULT 'NEMTAG'::character varying NOT NULL,
    usr_svie_primary_membership integer,
    usr_delegated boolean DEFAULT false NOT NULL,
    usr_show_recommended_photo boolean DEFAULT false NOT NULL,
    usr_screen_name character varying(50) NOT NULL,
    usr_date_of_birth date,
    usr_gender character varying(50) NOT NULL,
    usr_student_status character varying(50) NOT NULL,
    usr_mother_name character varying(100),
    usr_photo_path character varying(255),
    usr_webpage character varying(255),
    usr_cell_phone character varying(15),
    usr_home_address character varying(255),
    usr_est_grad character(9),
    usr_dormitory character varying(50),
    usr_room character varying(10),
    usr_confirm character(64)
);


--
-- Name: usr_private_attrs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE usr_private_attrs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_with_oids = false;

--
-- Name: usr_private_attrs; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE usr_private_attrs (
    id bigint DEFAULT nextval('usr_private_attrs_id_seq'::regclass) NOT NULL,
    usr_id bigint NOT NULL,
    attr_name character varying(64) NOT NULL,
    visible boolean DEFAULT false NOT NULL
);


--
-- Name: belepoigenyles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY belepoigenyles
    ADD CONSTRAINT belepoigenyles_pkey PRIMARY KEY (id);


--
-- Name: ertekeles_uzenet_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY ertekeles_uzenet
    ADD CONSTRAINT ertekeles_uzenet_pkey PRIMARY KEY (id);


--
-- Name: ertekelesek_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY ertekelesek
    ADD CONSTRAINT ertekelesek_pkey PRIMARY KEY (id);


--
-- Name: event_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY event
    ADD CONSTRAINT event_pkey PRIMARY KEY (evt_id);


--
-- Name: groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (grp_id);


--
-- Name: grp_membership_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY grp_membership
    ADD CONSTRAINT grp_membership_pkey PRIMARY KEY (id);


--
-- Name: im_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY im_accounts
    ADD CONSTRAINT im_accounts_pkey PRIMARY KEY (id);


--
-- Name: log_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: pl; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY neptun_list
    ADD CONSTRAINT pl PRIMARY KEY (neptun);


--
-- Name: pontigenyles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pontigenyles
    ADD CONSTRAINT pontigenyles_pkey PRIMARY KEY (id);


--
-- Name: poszt_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY poszt
    ADD CONSTRAINT poszt_pkey PRIMARY KEY (id);


--
-- Name: poszttipus_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY poszttipus
    ADD CONSTRAINT poszttipus_pkey PRIMARY KEY (pttip_id);


--
-- Name: spot_images_usr_neptun_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY spot_images
    ADD CONSTRAINT spot_images_usr_neptun_key UNIQUE (usr_neptun);


--
-- Name: system_attrs_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY system_attrs
    ADD CONSTRAINT system_attrs_pkey PRIMARY KEY (attributeid);


--
-- Name: unique_memberships; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY grp_membership
    ADD CONSTRAINT unique_memberships UNIQUE (grp_id, usr_id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (usr_id);


--
-- Name: users_usr_neptun_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_usr_neptun_key UNIQUE (usr_neptun);


--
-- Name: users_usr_screen_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_usr_screen_name_key UNIQUE (usr_screen_name);


--
-- Name: usr_private_attrs_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY usr_private_attrs
    ADD CONSTRAINT usr_private_attrs_pkey PRIMARY KEY (id);


--
-- Name: bel_tipus_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX bel_tipus_idx ON belepoigenyles USING btree (belepo_tipus);


--
-- Name: ert_semester_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX ert_semester_idx ON ertekelesek USING btree (semester);


--
-- Name: fki_felado_usr_id; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_felado_usr_id ON ertekeles_uzenet USING btree (felado_usr_id);


--
-- Name: fki_group_id; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_group_id ON ertekeles_uzenet USING btree (group_id);


--
-- Name: groups_grp_id_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE UNIQUE INDEX groups_grp_id_idx ON groups USING btree (grp_id);


--
-- Name: idx_groups_grp_name; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX idx_groups_grp_name ON groups USING btree (grp_name);


--
-- Name: idx_groups_grp_type; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX idx_groups_grp_type ON groups USING btree (grp_type);


--
-- Name: membership_usr_fk_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX membership_usr_fk_idx ON grp_membership USING btree (usr_id);


--
-- Name: next_version_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX next_version_idx ON ertekelesek USING btree (next_version NULLS FIRST);


--
-- Name: poszt_fk_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX poszt_fk_idx ON poszt USING btree (grp_member_id);


--
-- Name: unique_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE UNIQUE INDEX unique_idx ON ertekelesek USING btree (grp_id, semester, next_version NULLS FIRST);


--
-- Name: users_neptun; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX users_neptun ON users USING btree (usr_neptun);


--
-- Name: users_usr_id_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE UNIQUE INDEX users_usr_id_idx ON users USING btree (usr_id);


--
-- Name: users_usr_neptun_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE UNIQUE INDEX users_usr_neptun_idx ON users USING btree (usr_neptun);


--
-- Name: after_insert; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER after_insert AFTER INSERT ON spot_images FOR EACH ROW EXECUTE PROCEDURE update_user_recommended_photo_after_insert();


--
-- Name: before_delete; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER before_delete BEFORE DELETE ON spot_images FOR EACH ROW EXECUTE PROCEDURE update_user_recommended_photo_before_delete();


--
-- Name: delete_oids; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER delete_oids AFTER DELETE ON spot_images FOR EACH ROW EXECUTE PROCEDURE delete_oids();


--
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT "$1" FOREIGN KEY (grp_parent) REFERENCES groups(grp_id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- Name: fk4e301ac36958e716; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY belepoigenyles
    ADD CONSTRAINT fk4e301ac36958e716 FOREIGN KEY (usr_id) REFERENCES users(usr_id);


--
-- Name: fk807db18871c0d156; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ertekelesek
    ADD CONSTRAINT fk807db18871c0d156 FOREIGN KEY (felado_usr_id) REFERENCES users(usr_id);


--
-- Name: fk807db18879696582; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ertekelesek
    ADD CONSTRAINT fk807db18879696582 FOREIGN KEY (grp_id) REFERENCES groups(grp_id);


--
-- Name: fk807db188b31cf015; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ertekelesek
    ADD CONSTRAINT fk807db188b31cf015 FOREIGN KEY (elbiralo_usr_id) REFERENCES users(usr_id);


--
-- Name: fk_ertekeles_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY belepoigenyles
    ADD CONSTRAINT fk_ertekeles_id FOREIGN KEY (ertekeles_id) REFERENCES ertekelesek(id) ON DELETE CASCADE;


--
-- Name: fk_ertekeles_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pontigenyles
    ADD CONSTRAINT fk_ertekeles_id FOREIGN KEY (ertekeles_id) REFERENCES ertekelesek(id) ON DELETE CASCADE;


--
-- Name: fk_felado_usr_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ertekeles_uzenet
    ADD CONSTRAINT fk_felado_usr_id FOREIGN KEY (felado_usr_id) REFERENCES users(usr_id) ON DELETE SET NULL;


--
-- Name: fk_group_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ertekeles_uzenet
    ADD CONSTRAINT fk_group_id FOREIGN KEY (group_id) REFERENCES groups(grp_id) ON DELETE CASCADE;


--
-- Name: fk_next_version; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ertekelesek
    ADD CONSTRAINT fk_next_version FOREIGN KEY (next_version) REFERENCES ertekelesek(id) ON DELETE SET NULL;


--
-- Name: fkaa1034cd6958e716; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pontigenyles
    ADD CONSTRAINT fkaa1034cd6958e716 FOREIGN KEY (usr_id) REFERENCES users(usr_id);


--
-- Name: grp_membership_grp_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY grp_membership
    ADD CONSTRAINT grp_membership_grp_id_fkey FOREIGN KEY (grp_id) REFERENCES groups(grp_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: grp_membership_usr_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY grp_membership
    ADD CONSTRAINT grp_membership_usr_id_fkey FOREIGN KEY (usr_id) REFERENCES users(usr_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: im_accounts_usr_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY im_accounts
    ADD CONSTRAINT im_accounts_usr_id_fkey FOREIGN KEY (usr_id) REFERENCES users(usr_id);


--
-- Name: log_event; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_event FOREIGN KEY (evt_id) REFERENCES event(evt_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: log_group; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_group FOREIGN KEY (grp_id) REFERENCES groups(grp_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: log_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_user FOREIGN KEY (usr_id) REFERENCES users(usr_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: poszt_grp_member_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY poszt
    ADD CONSTRAINT poszt_grp_member_fk FOREIGN KEY (grp_member_id) REFERENCES grp_membership(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: poszt_pttip_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY poszt
    ADD CONSTRAINT poszt_pttip_fk FOREIGN KEY (pttip_id) REFERENCES poszttipus(pttip_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: poszttipus_opc_csoport; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY poszttipus
    ADD CONSTRAINT poszttipus_opc_csoport FOREIGN KEY (grp_id) REFERENCES groups(grp_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: users_main_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_main_group_fkey FOREIGN KEY (usr_svie_primary_membership) REFERENCES grp_membership(id);


--
-- Name: usr_private_attrs_usr_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usr_private_attrs
    ADD CONSTRAINT usr_private_attrs_usr_id_fkey FOREIGN KEY (usr_id) REFERENCES users(usr_id);


--
-- PostgreSQL database dump complete
--

