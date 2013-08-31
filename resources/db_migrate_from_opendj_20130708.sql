-- drop unused tables and views
DROP TABLE security_tokens;
DROP TABLE neptun_list_aktivfelev;
DROP VIEW  users_full_dormitory;
DROP TABLE user_attrs;
DROP TABLE users_svie_temp;

-- remove unused attributes
ALTER TABLE users DROP COLUMN usr_passwd;
ALTER TABLE users DROP COLUMN usr_sss_token;
ALTER TABLE users DROP COLUMN usr_sss_token_logintime ;

CREATE SEQUENCE screen_name_seq;

-- add attributes to user that were in opendj before 2013.07.
ALTER TABLE users ADD COLUMN usr_screen_name varchar(50) UNIQUE NOT NULL DEFAULT 'user' || nextval('screen_name_seq')::text; -- username, LDAP attr was 'uid'
ALTER TABLE users ADD COLUMN usr_date_of_birth date;
ALTER TABLE users ADD COLUMN usr_gender varchar(50) NOT NULL DEFAULT 'NOTSPECIFIED';
ALTER TABLE users ADD COLUMN usr_student_status varchar(50) NOT NULL DEFAULT 'UNKNOWN';
ALTER TABLE users ADD COLUMN usr_mother_name varchar(100);
ALTER TABLE users ADD COLUMN usr_photo_path varchar(255);
ALTER TABLE users ADD COLUMN usr_webpage varchar(255);
ALTER TABLE users ADD COLUMN usr_cell_phone varchar(50);
ALTER TABLE users ADD COLUMN usr_home_address varchar(255);
ALTER TABLE users ADD COLUMN usr_est_grad char(10); -- format: YYYYYYYY/[12]
ALTER TABLE users ADD COLUMN usr_dormitory varchar(50);
ALTER TABLE users ADD COLUMN usr_room varchar(10);
ALTER TABLE users ADD COLUMN usr_confirm char(64); -- confirmation code for registration
ALTER TABLE users ADD COLUMN usr_status varchar(8) NOT NULL DEFAULT 'INACTIVE'; -- ACTIVE or INACTIVE, was inetUserStatus in DS
ALTER TABLE users ADD COLUMN usr_password char(28); -- password hash base64 encoded
ALTER TABLE users ADD COLUMN usr_salt char(12); -- password salt base64 encoded

-- change users id type to bigint
ALTER TABLE users ALTER COLUMN usr_id TYPE bigint;


-- delete everything from spot_images first
DELETE FROM spot_images;

-- altering spot images to have image path instead of a blob
ALTER TABLE spot_images DROP COLUMN image;
ALTER TABLE spot_images ADD COLUMN image_path varchar(255) NOT NULL;
DROP FUNCTION delete_oids () CASCADE;

-- private attributes for users
CREATE SEQUENCE usr_private_attrs_id_seq;
CREATE TABLE usr_private_attrs (
    id bigint DEFAULT nextval('usr_private_attrs_id_seq') PRIMARY KEY,
    usr_id bigint REFERENCES users(usr_id) NOT NULL, -- id of the user
    attr_name varchar(64) NOT NULL, -- the name of the attribute, java enum
    visible boolean NOT NULL DEFAULT false -- attribute visibility
);

-- IMAccout -> new entity
CREATE SEQUENCE im_accounts_seq;
CREATE TABLE im_accounts (
    id bigint DEFAULT nextval('im_accounts_seq') PRIMARY KEY,
    protocol varchar(50) NOT NULL,
    account_name varchar(255) NOT NULL,
    usr_id integer REFERENCES users(usr_id)
);

-- TODO alter tables to have bigint id


-- cleanup
ALTER TABLE users ALTER usr_screen_name DROP DEFAULT;
DROP SEQUENCE screen_name_seq CASCADE;
