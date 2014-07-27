-- make important contact information visible
UPDATE usr_private_attrs SET visible = 't' WHERE attr_name IN ('CELL_PHONE', 'EMAIL', 'SCREEN_NAME', 'ROOM_NUMBER');
