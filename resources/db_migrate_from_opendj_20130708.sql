-- drop unused tables
DROP TABLE security_tokens;
DROP TABLE neptun_list_aktivfelev;

-- remove unused attributes
ALTER TABLE users DROP COLUMN usr_passwd;
ALTER TABLE users DROP COLUMN usr_sss_token;
ALTER TABLE users DROP COLUMN usr_sss_token_logintime ;

-- add attributes to user that were in opendj before 2013.07.
ALTER TABLE users ADD COLUMN usr_date_of_birth date;
ALTER TABLE users ADD COLUMN usr_gender varchar(50) NOT NULL;
ALTER TABLE users ADD COLUMN usr_student_status varchar(50) NOT NULL;
ALTER TABLE users ADD COLUMN usr_mother_name varchar(100);
ALTER TABLE users ADD COLUMN usr_photo_path varchar(255);
ALTER TABLE users ADD COLUMN usr_webpage varchar(255);
ALTER TABLE users ADD COLUMN usr_cell_phone varchar(255);
ALTER TABLE users ADD COLUMN usr_home_address varchar(255);
ALTER TABLE users ADD COLUMN usr_est_grad char(9); -- format: YYYYYYYY[12]
ALTER TABLE users ADD COLUMN usr_dormitory varchar(50);
ALTER TABLE users ADD COLUMN usr_room smallint;

CREATE SEQUENCE im_accounts_seq;

-- IMAccout -> new entity
CREATE TABLE im_accounts (
    id bigint DEFAULT nextval('im_accounts_seq') PRIMARY KEY,
    protocol varchar(50) NOT NULL,
    screen_name varchar(255) NOT NULL,
    usr_id integer references users(usr_id)
);

-- TODO alter tables to have bigint id