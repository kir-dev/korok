CREATE TABLE spot_images (
 usr_neptun character(6) NOT NULL UNIQUE,
 image oid NOT NULL
);

CREATE OR REPLACE FUNCTION delete_oids() RETURNS trigger AS '
BEGIN
  PERFORM lo_unlink(OLD.image);
  RETURN OLD;
END
' LANGUAGE plpgsql;

CREATE TRIGGER delete_oids AFTER DELETE ON spot_images FOR EACH ROW EXECUTE PROCEDURE delete_oids();
