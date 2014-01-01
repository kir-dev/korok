-- drop trigger for remove
DROP TRIGGER before_delete ON spot_images;
DROP FUNCTION update_user_recommended_photo_before_delete();