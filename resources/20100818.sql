ALTER TABLE ertekeles_uzenet
   ADD COLUMN group_id integer DEFAULT NULL;
ALTER TABLE ertekeles_uzenet
   ADD COLUMN semester character(9) NOT NULL DEFAULT '';

ALTER TABLE ertekeles_uzenet ADD CONSTRAINT fk_group_id FOREIGN KEY (group_id) REFERENCES groups (grp_id)
   ON UPDATE NO ACTION ON DELETE CASCADE;
CREATE INDEX fki_group_id ON ertekeles_uzenet(group_id);

UPDATE ertekeles_uzenet
   SET group_id = v.grp_id, semester = v.semester
      FROM ertekelesek v
      WHERE v.id = ertekeles_uzenet.ertekeles_id;

ALTER TABLE ertekeles_uzenet
   DROP COLUMN ertekeles_id;

ALTER TABLE ertekeles_uzenet
   ADD COLUMN from_system boolean DEFAULT FALSE;
