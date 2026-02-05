-- Migration: Update SELLER role to VENDOR
-- Date: 2026-02-05
-- Description: Rename SELLER role to VENDOR in users table

-- Update all existing users with SELLER role to VENDOR role
UPDATE users 
SET role = 'VENDOR' 
WHERE role = 'SELLER';

-- Update email addresses for consistency (optional)
UPDATE users 
SET email = 'vendor@smartshop.com',
    last_name = 'Vendor'
WHERE email = 'seller@smartshop.com';

UPDATE users 
SET last_name = 'Wilson'
WHERE email = 'robert.wilson@smartshop.com' AND role = 'VENDOR';

-- Verify the changes
SELECT id, first_name, last_name, email, role 
FROM users 
WHERE role = 'VENDOR' 
ORDER BY id;
