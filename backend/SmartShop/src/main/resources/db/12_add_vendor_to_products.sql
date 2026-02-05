-- Add vendor_id column to products table to track which vendor owns each product
ALTER TABLE products ADD COLUMN IF NOT EXISTS vendor_id BIGINT;

-- Add foreign key constraint if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_products_vendor') THEN
        ALTER TABLE products ADD CONSTRAINT fk_products_vendor FOREIGN KEY (vendor_id) REFERENCES users(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_products_vendor_id ON products(vendor_id);

-- Update existing products to be owned by the first vendor (or set to NULL if no vendor exists)
UPDATE products SET vendor_id = (SELECT id FROM users WHERE role = 'VENDOR' LIMIT 1) WHERE vendor_id IS NULL;
