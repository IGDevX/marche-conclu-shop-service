-- Drop the global UNIQUE constraint on label
ALTER TABLE shelf
DROP CONSTRAINT IF EXISTS shelf_label_key;
-- Add composite UNIQUE constraint on (producer_id, label)
-- This allows different producers to have shelves with the same label
ALTER TABLE shelf
ADD CONSTRAINT uq_shelf_producer_label UNIQUE (producer_id, label);

-- Add a comment to document the constraint
COMMENT ON CONSTRAINT uq_shelf_producer_label ON shelf IS 'Ensures shelf labels are unique per producer';
