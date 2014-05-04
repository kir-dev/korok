-- drop trigger for remove
DROP TRIGGER before_delete ON spot_images;
DROP FUNCTION update_user_recommended_photo_before_delete();

-- drop trigger for insert
DROP TRIGGER after_insert ON spot_images;
DROP FUNCTION update_user_recommended_photo_after_insert();

-- drop entrant export function
DROP FUNCTION export_entrant_requests(text, text, integer);
DROP TYPE exported_entrant_request;

-- drop point export function
DROP FUNCTION getpointsforsemester(text, text);
DROP TYPE user_points;

-- new table for storing point hitory
CREATE SEQUENCE point_history_seq;
CREATE TABLE point_history (
    id bigint DEFAULT nextval('point_history_seq') PRIMARY KEY,
    usr_id bigint REFERENCES users NOT NULL, -- user
    point integer NOT NULL, -- point for the semester
    semester varchar(9) NOT NULL -- semester
);
