-- modify the schema for issue vir_postprocessing#1 - https://github.com/kir-dev/vir_postprocessing/issues/1
-- add a column to users table which we update at every login in the vir-auth module

BEGIN;
alter table users add column usr_lastlogin timestamp without time zone default NULL;
COMMIT;
