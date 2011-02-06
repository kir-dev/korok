CREATE OR REPLACE FUNCTION check_photo_flag() RETURNS trigger AS $BODY$
DECLARE
  related boolean;
BEGIN
  related = FALSE;
  IF (TG_OP = 'INSERT') THEN
    related = TRUE;
  ELSEIF (TG_OP = 'UPDATE' AND OLD.usr_neptun <> NEW.usr_neptun) THEN
    related = TRUE;
  END IF;

  IF (related = TRUE) THEN
    IF ((SELECT COUNT(*) FROM spot_images WHERE usr_neptun = NEW.usr_neptun) <> 0) THEN
      UPDATE users SET usr_show_recommended_photo = TRUE WHERE usr_id = NEW.usr_id;
    END IF;
  END IF;

  RETURN NULL;
END
$BODY$
LANGUAGE plpgsql;

CREATE TRIGGER check_photo_flag AFTER INSERT OR UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE check_photo_flag();
