USE retail_inventory;

CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 10,
    category VARCHAR(50),
    supplier VARCHAR(100),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

TRUNCATE TABLE products;
INSERT INTO products (product_code, product_name, description, price, quantity, reorder_level, category, supplier) VALUES
('P001', 'T-Shirt Basic', 'Cotton round neck t-shirt', 899.00, 100, 20, 'Clothing', 'Fashion Supplier Inc.'),
('P002', 'Jeans Regular', 'Regular fit blue jeans', 1499.00, 50, 15, 'Clothing', 'Denim Masters'),
('P003', 'Coffee Mug', 'Ceramic coffee mug', 349.00, 75, 25, 'Kitchenware', 'Home Goods Ltd.'),
('P004', 'Notebook', 'Spiral-bound lined notebook', 199.00, 200, 50, 'Stationery', 'Paper Products Co.'),
('P005', 'Wireless Mouse', 'Bluetooth wireless mouse', 999.00, 30, 10, 'Electronics', 'Tech Innovations'),
('P006', 'Headphones', 'Over-ear wireless headphones', 2499.00, 25, 8, 'Electronics', 'Audio Solutions'),
('P007', 'Water Bottle', 'Stainless steel insulated bottle', 599.00, 80, 15, 'Kitchenware', 'Home Goods Ltd.'),
('P008', 'Backpack', 'Water-resistant laptop backpack', 1299.00, 40, 10, 'Accessories', 'Travel Gear Co.'),
('P009', 'Desk Lamp', 'LED adjustable desk lamp', 799.00, 35, 12, 'Home Decor', 'Lighting Experts'),
('P010', 'Smartphone Case', 'Protective phone case', 399.00, 120, 25, 'Accessories', 'Mobile Accessories Inc.'),
('P011', 'Hand Sanitizer', '500ml alcohol-based sanitizer', 149.00, 150, 30, 'Health', 'Health Essentials'),
('P012', 'Face Mask', 'Pack of 10 disposable masks', 199.00, 200, 50, 'Health', 'Health Essentials'),
('P013', 'Power Bank', '10000mAh fast charging', 1299.00, 45, 15, 'Electronics', 'Power Solutions'),
('P014', 'Sunglasses', 'UV protection sunglasses', 899.00, 30, 10, 'Accessories', 'Fashion Supplier Inc.'),
('P015', 'Wall Clock', 'Modern design wall clock', 649.00, 25, 8, 'Home Decor', 'Home Goods Ltd.');



-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- Storing hashed passwords
    full_name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Add user_id column to products table to link products with users
ALTER TABLE products ADD COLUMN user_id INT;

-- Add foreign key constraint
ALTER TABLE products
ADD CONSTRAINT fk_product_user
FOREIGN KEY (user_id) REFERENCES users(user_id)
ON DELETE SET NULL;

-- Create some sample users
INSERT INTO users (username, password, full_name, email) VALUES
('admin', 'admin123', 'Admin User', 'admin@example.com'),
('user1', 'user123', 'Regular User 1', 'user1@example.com'),
('user2', 'user234', 'Regular User 2', 'user2@example.com');

-- Assign products to users
UPDATE products SET user_id = 1 WHERE product_code IN ('P001', 'P002', 'P003', 'P004', 'P005');
UPDATE products SET user_id = 2 WHERE product_code IN ('P006', 'P007', 'P008', 'P009', 'P010');
UPDATE products SET user_id = 3 WHERE product_code IN ('P011', 'P012', 'P013', 'P014', 'P015');

select * from users;


-- Modify users table to add gender and address fields
ALTER TABLE users 
ADD COLUMN gender VARCHAR(10),
ADD COLUMN address TEXT;

-- Update existing users with default gender and address values
UPDATE users SET gender = 'Male', address = 'Not provided' WHERE user_id = 1;
UPDATE users SET gender = 'Female', address = 'Not provided' WHERE user_id = 2;
UPDATE users SET gender = 'Male', address = 'Not provided' WHERE user_id = 3;

-- Verify the changes
SELECT user_id, username, full_name, email, gender, address FROM users;
Delete from  users where username = 'ashutosh';
alter table users
drop column created_at,
drop column last_login;

UPDATE products SET user_id = 5 WHERE product_code IN ('P011', 'P012', 'P013', 'P014', 'P015');
