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

-- add attributes to user that were in opendj before 2013.07.
ALTER TABLE users ADD COLUMN usr_screen_name varchar(50) UNIQUE NOT NULL; -- username, LDAP attr was 'uid'
ALTER TABLE users ADD COLUMN usr_date_of_birth date;
ALTER TABLE users ADD COLUMN usr_gender varchar(50) NOT NULL;
ALTER TABLE users ADD COLUMN usr_student_status varchar(50) NOT NULL;
ALTER TABLE users ADD COLUMN usr_mother_name varchar(100);
ALTER TABLE users ADD COLUMN usr_photo_path varchar(255);
ALTER TABLE users ADD COLUMN usr_webpage varchar(255);
ALTER TABLE users ADD COLUMN usr_cell_phone varchar(15);
ALTER TABLE users ADD COLUMN usr_home_address varchar(255);
ALTER TABLE users ADD COLUMN usr_est_grad char(9); -- format: YYYYYYYY[12]
ALTER TABLE users ADD COLUMN usr_dormitory varchar(50);
ALTER TABLE users ADD COLUMN usr_room varchar(10);
ALTER TABLE users ADD COLUMN usr_confirm char(64); -- confirmation code for registration

-- chnage users id type to biging
ALTER TABLE users ALTER COLUMN usr_id TYPE bigint;

-- private attributes for users
CREATE TABLE usr_private_attrs (
    usr_id bigint REFERENCES users(usr_id) NOT NULL, -- id of the user
    attr_name varchar(64) NOT NULL, -- the name of the attribute, java enum
    visible boolean NOT NULL DEFAULT false, -- attribute visibility
    PRIMARY KEY(usr_id, attr_name)
);

-- IMAccout -> new entity
CREATE SEQUENCE im_accounts_seq;
CREATE TABLE im_accounts (
    id bigint DEFAULT nextval('im_accounts_seq') PRIMARY KEY,
    protocol varchar(50) NOT NULL,
    screen_name varchar(255) NOT NULL,
    usr_id integer REFERENCES users(usr_id)
);

-- TODO alter tables to have bigint id