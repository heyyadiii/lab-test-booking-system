-- ============================================
-- Multi-Lab Support - Database Migration
-- ============================================
-- Run this in MySQL Workbench or phpMyAdmin
-- Database: lab_test_db
-- ============================================

USE lab_test_db;

-- Step 1: Create laboratories table
CREATE TABLE IF NOT EXISTS laboratories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(6) NOT NULL,
    phone VARCHAR(10),
    email VARCHAR(255),
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    INDEX idx_city (city),
    INDEX idx_state (state),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Step 2: Insert sample laboratories
INSERT INTO laboratories (name, address, city, state, pincode, phone, email, description, active, created_at) VALUES
('LabFlow Central Lab', '123 Medical Plaza, Sector 15', 'Delhi', 'Delhi', '110001', '9876543210', 'central@labflow.com', 'Main laboratory with all facilities', TRUE, NOW()),
('LabFlow South Branch', '456 Health Avenue, Malviya Nagar', 'Delhi', 'Delhi', '110017', '9876543211', 'south@labflow.com', 'South Delhi branch with quick service', TRUE, NOW()),
('LabFlow Mumbai Lab', '789 Wellness Street, Andheri', 'Mumbai', 'Maharashtra', '400053', '9876543212', 'mumbai@labflow.com', 'Mumbai branch with advanced equipment', TRUE, NOW()),
('LabFlow Bangalore Lab', '321 Tech Park Road, Whitefield', 'Bangalore', 'Karnataka', '560066', '9876543213', 'bangalore@labflow.com', 'Bangalore branch near IT hub', TRUE, NOW());

-- Step 3: Add laboratory_id column to time_slots table
ALTER TABLE time_slots 
ADD COLUMN laboratory_id BIGINT AFTER id;

-- Step 4: Set default laboratory for existing time slots (assign to first lab)
UPDATE time_slots 
SET laboratory_id = (SELECT id FROM laboratories LIMIT 1)
WHERE laboratory_id IS NULL;

-- Step 5: Make laboratory_id NOT NULL and add foreign key
ALTER TABLE time_slots 
MODIFY COLUMN laboratory_id BIGINT NOT NULL,
ADD CONSTRAINT fk_timeslot_laboratory 
    FOREIGN KEY (laboratory_id) REFERENCES laboratories(id);

-- Step 6: Add index for better query performance
CREATE INDEX idx_timeslot_laboratory ON time_slots(laboratory_id);

-- Step 7: Verify the changes
DESCRIBE laboratories;
DESCRIBE time_slots;

SELECT 'Multi-Lab Migration completed successfully!' AS Status;
SELECT COUNT(*) AS 'Total Laboratories' FROM laboratories;
SELECT COUNT(*) AS 'Total Time Slots' FROM time_slots;
