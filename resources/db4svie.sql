\echo Átalakítás megkezdése
\echo Felhasználói e-mail címek trimelése

update users set usr_email = trim(both ' ' from usr_email);

\echo Új mezők hozzáadása megfelelő constraint-ekkel

ALTER TABLE users ADD COLUMN usr_svie_state character varying(255) NOT NULL DEFAULT 'NEMTAG';
ALTER TABLE users ADD COLUMN usr_svie_member_type character varying(255) NOT NULL DEFAULT 'NEMTAG';
ALTER TABLE users ADD COLUMN usr_svie_primary_group integer;
ALTER TABLE groups ADD COLUMN grp_issvie boolean NOT NULL DEFAULT false;
ALTER TABLE groups ADD COLUMN grp_svie_delegate_nr integer;

\echo Régi elavult, nem használt mezők eltávolítása

ALTER TABLE groups DROP COLUMN grp_acc_cards;
ALTER TABLE groups DROP COLUMN grp_acc_points;
ALTER TABLE groups DROP COLUMN grp_flags;
ALTER TABLE groups DROP COLUMN is_del;
ALTER TABLE groups DROP COLUMN grp_shortname;
ALTER TABLE users DROP COLUMN usr_schaccount CASCADE;
ALTER TABLE users DROP COLUMN usr_passwd CASCADE;
ALTER TABLE users DROP COLUMN is_del CASCADE;
ALTER TABLE users DROP COLUMN need_confirm CASCADE;

\echo View újraelkészítése, ha esetleg más alkalmazásnak szüksége van rá

CREATE VIEW users_full_dormitory AS SELECT users.usr_id, users.usr_email, users.usr_neptun, users.usr_firstname, users.usr_lastname, users.usr_nickname, user_attrs.usr_rights, user_attrs.usr_idcard_number, user_attrs.usr_other_email, user_attrs.usr_country, user_attrs.usr_postcode, user_attrs.usr_city, user_attrs.usr_address, user_attrs.usr_phone, user_attrs.usr_mobil, user_attrs.usr_webpage, user_attrs.usr_photo, user_attrs.usr_icq, user_attrs.usr_uni_start, user_attrs.usr_uni_end, user_attrs.usr_status, user_attrs.usr_workplace, user_attrs.usr_is_a_girl, user_attrs.usr_fir_rnd, user_attrs.usr_invisible, user_attrs.usr_key, user_attrs.usr_host, user_attrs.usr_lastlogin, user_attrs.usr_last_modified, user_attrs.usr_jabber, ((users.usr_lastname || ' '::text) || users.usr_firstname) AS usr_name FROM (users JOIN user_attrs USING (usr_id));

\echo Új posztkezeléshez szükséges parancsok

create table poszttipus ( pttip_id integer, grp_id integer, pttip_name character varying(30) NOT NULL, PRIMARY KEY (pttip_id) );
alter table poszttipus add constraint poszttipus_opc_csoport FOREIGN KEY (grp_id ) REFERENCES groups (grp_id) ON UPDATE CASCADE ON DELETE CASCADE;
create table grp_membership ( id integer NOT NULL, grp_id integer, usr_id integer, membership_start date DEFAULT now(), membership_end date, PRIMARY KEY (id));
ALTER TABLE users ADD CONSTRAINT users_main_group FOREIGN KEY ( usr_svie_primary_group ) REFERENCES grp_membership ( id ) ON UPDATE CASCADE ON DELETE CASCADE;
create sequence grp_members_seq;
alter table grp_membership alter column id set default nextval('grp_members_seq'::regclass);
insert into grp_membership (grp_id, usr_id, membership_start, membership_end) (SELECT grp_id, usr_id, membership_start, membership_end FROM grp_members);
create table poszt (id integer, grp_member_id integer, pttip_id integer, PRIMARY KEY ( id )  );
alter table poszt add constraint poszt_grp_member_fk FOREIGN KEY ( grp_member_id ) REFERENCES grp_membership (id) ON UPDATE CASCADE ON DELETE CASCADE;
alter table poszt add constraint poszt_pttip_fk FOREIGN KEY ( pttip_id ) REFERENCES poszttipus (pttip_id) ON UPDATE CASCADE ON DELETE CASCADE;
create sequence poszt_seq;
create sequence poszttipus_seq;
alter table poszt alter column id set default nextval('poszt_seq'::regClass);
alter table poszttipus alter column pttip_id set default nextval('poszttipus_seq'::regClass);
insert into poszttipus (grp_id, pttip_name) values (NULL, 'gazdaságis');
insert into poszttipus (grp_id, pttip_name) values (NULL, 'PR menedzser');
insert into poszttipus (grp_id, pttip_name) values (NULL, 'körvezető');
insert into poszttipus (grp_id, pttip_name) values (NULL, 'volt körvezető');
insert into poszttipus (grp_id, pttip_name) values (NULL, 'vendégfogadó');
insert into poszttipus (grp_id, pttip_name) values (NULL, 'feldolgozás alatt');

\echo Tagságtípusok átkonvertálása az új formátumra

insert into poszt (grp_member_id, pttip_id) (select id, (select pttip_id from poszttipus where pttip_name = 'körvezető')  from grp_membership where (grp_id, usr_id)  IN  (select grp_id, usr_id from grp_members where member_rights & 1 = 1));
insert into poszt (grp_member_id, pttip_id) (select id, (select pttip_id from poszttipus where pttip_name = 'volt körvezető') from grp_membership where (grp_id, usr_id)  IN  (select grp_id, usr_id from grp_members where member_rights & 2 = 2));
insert into poszt (grp_member_id, pttip_id) (select id, (select pttip_id from poszttipus where pttip_name = 'gazdaságis') from grp_membership where (grp_id, usr_id)  IN  (select grp_id, usr_id from grp_members where member_rights & 4 = 4));
insert into poszt (grp_member_id, pttip_id) (select id, (select pttip_id from poszttipus where pttip_name = 'PR menedzser') from grp_membership where (grp_id, usr_id)  IN  (select grp_id, usr_id from grp_members where member_rights & 8 = 8));
insert into poszt (grp_member_id, pttip_id) (select id, (select pttip_id from poszttipus where pttip_name = 'vendégfogadó') from grp_membership where (grp_id, usr_id)  IN  (select grp_id, usr_id from grp_members where member_rights & 16 = 16));
insert into poszt (grp_member_id, pttip_id) (select id, (select pttip_id from poszttipus where pttip_name = 'feldolgozás alatt') from grp_membership where (grp_id, usr_id)  IN  (select grp_id, usr_id from grp_members where member_rights & 32768 = 32768));

\echo Ismeretlen tagságtípusú emberkét át kell konvertálni sima taggá

insert into poszt (grp_member_id, pttip_id) (select id, (select pttip_id from poszttipus where pttip_name = 'tag')  from grp_membership where (grp_id, usr_id ) IN (select grp_id, usr_id from grp_members where member_rights & 16384 = 16384));

\echo indexek létrehozása

create index poszt_fk_idx ON poszt ( grp_member_id );
create index membership_usr_fk_idx ON grp_membership (usr_id);

\echo SVIE posztok hozzáadása

insert into poszttipus ( grp_id, pttip_name) values ( (select grp_id from groups where grp_name = 'SVIE'), 'adminisztrátor');
insert into poszttipus ( grp_id, pttip_name) values ( (select grp_id from groups where grp_name = 'SVIE'), 'választmányi elnök');

\echo Logolási rendszer előállítása

create table event ( evt_id integer NOT NULL, evt_text character varying(30), PRIMARY KEY (evt_id) );
create table log ( id integer NOT NULL,  grp_id integer, usr_id integer NOT NULL, evt_id integer NOT NULL, evt_date date DEFAULT now(), PRIMARY KEY (id));
create sequence event_seq;
create sequence log_seq;
alter table event alter COLUMN evt_id SET default nextval('event_seq'::regClass);
alter table log alter COLUMN id SET default nextval('log_seq'::regClass);
alter table log ADD CONSTRAINT log_group FOREIGN KEY (grp_id) REFERENCES groups (grp_id) ON UPDATE CASCADE ON DELETE CASCADE;
alter table log ADD CONSTRAINT log_user FOREIGN KEY (usr_id) REFERENCES users (usr_id) ON UPDATE CASCADE ON DELETE CASCADE;
alter table log ADD CONSTRAINT log_event FOREIGN KEY (evt_id) REFERENCES event (evt_id) ON UPDATE CASCADE ON DELETE CASCADE;
INSERT INTO system_attrs VALUES (136902, 'utolso_log_kuldve', '2009-08-26');
INSERT INTO event (evt_text) VALUES ('JELENTKEZES');
INSERT INTO event (evt_text) VALUES ('TAGSAGTORLES');
insert into event (evt_text) VALUES ('SVIE_JELENTKEZES');
insert into event (evt_text) VALUES ('SVIE_TAGSAGTORLES');
insert into event (evt_text) VALUES ('PARTOLOVAVALAS');
insert into event (evt_text) VALUES ('RENDESTAGGAVALAS');
insert into event (evt_text) VALUES ('ELFOGADASALATT');

ALTER TABLE users DROP CONSTRAINT users_main_group;
ALTER TABLE users ADD CONSTRAINT users_main_group_fkey FOREIGN KEY (usr_svie_primary_membership) REFERENCES grp_membership (id);
alter table grp_membership add constraint unique_memberships UNIQUE (grp_id, usr_id);
