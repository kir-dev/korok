ALTER TABLE users ADD COLUMN usr_auth_sch_id VARCHAR UNIQUE;
ALTER TABLE users ADD COLUMN usr_bme_id VARCHAR UNIQUE;
ALTER TABLE users ADD COLUMN usr_created_at timestamp;
