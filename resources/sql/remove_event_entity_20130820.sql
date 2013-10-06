-- modify the schema for issue #39 - https://github.com/kir-dev/korok/issues/39

BEGIN;

-- add new column which will contains the enum (EventType) value as text
alter table log add column event varchar(30);

-- copy values to the new column
update log set event=(select evt.evt_text from event as evt where evt.evt_id=log.evt_id);

-- set not null constraint to the new column
alter table log alter column event set not null;

-- drop the old column
alter table log drop COLUMN evt_id;

-- drop the deprecated table
drop table event;

COMMIT;
