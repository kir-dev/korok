-- drop trigger for remove
DROP TRIGGER before_delete ON spot_images;
DROP FUNCTION update_user_recommended_photo_before_delete();

-- drop trigger for insert
DROP TRIGGER after_insert ON spot_images;
DROP FUNCTION update_user_recommended_photo_after_insert();

-- drop entrant export function
DROP FUNCTION export_entrant_requests(text, text, integer);
DROP TYPE exported_entrant_request;
