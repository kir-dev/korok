ALTER TABLE ertekelesek ADD COLUMN next_version bigint DEFAULT NULL;

ALTER TABLE ertekelesek
  ADD CONSTRAINT fk_next_version FOREIGN KEY (next_version)
      REFERENCES ertekelesek (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE SET NULL;

CREATE INDEX next_version_idx
   ON ertekelesek (next_version ASC NULLS FIRST);

CREATE UNIQUE INDEX unique_idx
   ON ertekelesek (grp_id ASC NULLS LAST, semester ASC NULLS LAST, next_version ASC NULLS FIRST);

ALTER TABLE ertekeles_uzenet DROP CONSTRAINT fk120ddcbaeb578707;
ALTER TABLE ertekeles_uzenet ADD CONSTRAINT fk_ertekeles_id FOREIGN KEY (ertekeles_id) REFERENCES ertekelesek (id)
   ON UPDATE NO ACTION ON DELETE CASCADE;
CREATE INDEX fki_ertekeles_id ON ertekeles_uzenet(ertekeles_id);

ALTER TABLE ertekeles_uzenet DROP CONSTRAINT fk120ddcba71c0d156;
ALTER TABLE ertekeles_uzenet ADD CONSTRAINT fk_felado_usr_id FOREIGN KEY (felado_usr_id) REFERENCES users (usr_id)
   ON UPDATE NO ACTION ON DELETE SET NULL;
CREATE INDEX fki_felado_usr_id ON ertekeles_uzenet(felado_usr_id);

ALTER TABLE ertekelesek
   ADD COLUMN explanation text DEFAULT NULL;

ALTER TABLE ertekelesek
   ADD COLUMN optlock integer NOT NULL DEFAULT 0;

ALTER TABLE belepoigenyles DROP CONSTRAINT fk4e301ac3eb578707;
ALTER TABLE belepoigenyles ADD CONSTRAINT fk_ertekeles_id FOREIGN KEY (ertekeles_id) REFERENCES ertekelesek (id)
   ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE pontigenyles DROP CONSTRAINT fkaa1034cdeb578707;
ALTER TABLE pontigenyles ADD CONSTRAINT fk_ertekeles_id FOREIGN KEY (ertekeles_id) REFERENCES ertekelesek (id)
   ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE ertekelesek
   ADD COLUMN is_considered boolean NOT NULL DEFAULT false;

