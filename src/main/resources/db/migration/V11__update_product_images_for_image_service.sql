-- Migration to replace old image fields with new image service fields
ALTER TABLE product
DROP COLUMN IF EXISTS image_url,
DROP COLUMN IF EXISTS image_key,
DROP COLUMN IF EXISTS image_thumbnail_url;

ALTER TABLE product
ADD COLUMN main_image_id UUID,
ADD COLUMN main_image_url VARCHAR(500);

COMMENT ON COLUMN product.main_image_id IS 'ID de l''image dans le service image';
COMMENT ON COLUMN product.main_image_url IS 'URL directe MinIO pour l''image principale';

