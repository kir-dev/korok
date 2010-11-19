ALTER TABLE users
   ADD COLUMN usr_show_recommended_photo boolean NOT NULL DEFAULT FALSE;

CREATE OR REPLACE FUNCTION update_user_recommended_photo_after_insert()
  RETURNS trigger AS $BODY$
DECLARE
BEGIN
  UPDATE users SET usr_show_recommended_photo = TRUE WHERE usr_neptun = NEW.usr_neptun;
END;
$BODY$
LANGUAGE plpgsql;

CREATE TRIGGER after_insert
  AFTER INSERT
  ON spot_images
  FOR EACH ROW
  EXECUTE PROCEDURE update_user_recommended_photo_after_insert();

CREATE OR REPLACE FUNCTION update_user_recommended_photo_before_delete()
  RETURNS trigger AS $BODY$
DECLARE
BEGIN
  UPDATE users SET usr_show_recommended_photo = FALSE WHERE usr_neptun = OLD.usr_neptun;
  RETURN OLD;
END;
$BODY$
LANGUAGE plpgsql;

CREATE TRIGGER before_delete
  BEFORE DELETE
  ON spot_images
  FOR EACH ROW
  EXECUTE PROCEDURE update_user_recommended_photo_before_delete();
