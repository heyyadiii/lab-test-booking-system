-- ============================================
-- PDF Report Generation - Database Migration
-- ============================================
-- Run this in MySQL Workbench or phpMyAdmin
-- Database: lab_test_db
-- ============================================

USE lab_test_db;

-- Step 1: Add findings column (required field)
ALTER TABLE reports 
ADD COLUMN findings TEXT AFTER results;

-- Step 2: Add recommendations column (optional field)
ALTER TABLE reports 
ADD COLUMN recommendations TEXT AFTER findings;

-- Step 3: Update existing reports with default values (if any exist)
UPDATE reports 
SET findings = 'Test completed successfully. Detailed analysis pending.',
    recommendations = 'Please consult with your physician for detailed interpretation.'
WHERE findings IS NULL OR findings = '';

-- Step 4: Make findings NOT NULL after setting defaults
ALTER TABLE reports 
MODIFY COLUMN findings TEXT NOT NULL;

-- Step 5: Verify the changes
DESCRIBE reports;

SELECT 'Migration completed successfully!' AS Status;
