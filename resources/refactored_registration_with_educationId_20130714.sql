-- convert char(6) to varchar(6)
ALTER TABLE neptun_list ALTER COLUMN neptun TYPE varchar(6);
-- add new columns to the neptun_list table
ALTER TABLE neptun_list ADD COLUMN education_id varchar(11) DEFAULT NULL;
ALTER TABLE neptun_list ADD COLUMN newbie BOOLEAN DEFAULT FALSE;
