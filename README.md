# Retail Shop Inventory Management System

A comprehensive Java-based inventory management solution for retail businesses with a user-friendly graphical interface.

## Overview

This application helps businesses efficiently manage their inventory by providing an intuitive interface to track products, monitor stock levels, and perform inventory management tasks. Built with Java Swing, the system offers robust features for complete inventory control.

## Features

### User Authentication
- Secure login system for multiple users
- User registration functionality
- Role-based access control

### Product Management
- **Create**: Add new products with details including name, code, price, quantity, category, and supplier
- **Read**: View and search all inventory items with filtering options
- **Update**: Modify product details and adjust inventory quantities
- **Delete**: Remove products from the inventory database

### Inventory Features
- Real-time inventory tracking
- Low stock alerts with visual indicators
- Inventory value calculation
- Categorization of products
- Supplier management
- Reorder level settings

### User Interface
- Clean, intuitive graphical user interface
- Sortable data tables for easy navigation
- Search functionality for quick product lookup
- Category filtering to organize product view
- Color-coded stock status indicators
- Quick quantity update feature

### Reporting
- Inventory summary statistics
- Total inventory value calculation
- Low stock item tracking

## Technical Details

### Architecture
- Follows MVC (Model-View-Controller) design pattern
- Modular code structure for maintainability

### Database
- MySQL database backend
- Secure connection handling
- Efficient data queries and manipulation

### Libraries Used
- Java Swing for the GUI components
- MySQL Connector/J for database connectivity
- rs2xml library for ResultSet to TableModel conversion

## System Requirements

- Java Runtime Environment (JRE) 8 or higher
- MySQL Server 5.7 or higher
- Minimum 4GB RAM
- Windows, macOS, or Linux operating system

## Installation

1. **Database Setup**:
   - Install MySQL Server if not already installed
   - Create a new database using the included `inventory.sql` script
   - Update database connection details if necessary in `DatabaseConnection.java`

2. **Application Setup**:
   - Ensure Java is installed on your system
   - Place the application JAR file in your preferred location
   - Ensure required JAR libraries (mysql-connector-java-8.0.17.jar, rs2xml.jar) are in the classpath

3. **Launch Application**:
   - Run the application using: `java -jar InventoryManagement.jar`
   - First-time users should register using the Registration form
   - Log in with your credentials to access the system

## Usage Guide

### Login and Registration
- New users can register by clicking the "Register" button on the login screen
- Existing users simply enter their username and password to log in

### Adding Products
1. Click the "Add Product" button
2. Fill in product details in the form:
   - Product Code (unique identifier)
   - Product Name
   - Description (optional)
   - Price
   - Quantity
   - Category
   - Supplier
   - Reorder Level (for low stock alerts)
3. Click "Save" to add the product to inventory

### Editing Products
1. Select a product from the table
2. Click "Edit Product" button
3. Modify the product information in the form that appears
4. Click "Save" to update the product details

### Deleting Products
1. Select a product from the table
2. Click "Delete Product" button
3. Confirm deletion when prompted

### Quick Quantity Updates
1. Select a product from the table
2. Enter new quantity in the "Quick Update Quantity" spinner
3. Click "Update" to immediately adjust the product's quantity

### Searching and Filtering
- Enter search terms in the search box to find products by code, name, or supplier
- Use the category dropdown to filter products by specific categories
- Click "Refresh" to reset filters and show all products

### Monitoring Stock Levels
- Products with quantities at or below their reorder level appear highlighted in light red
- The "Low Stock Items" counter in the summary panel shows the total number of items needing replenishment

## Troubleshooting

### Common Issues
- **Database Connection Failed**: Verify MySQL server is running and connection details are correct
- **Login Issues**: Ensure username and password are entered correctly
- **Missing Libraries**: Verify all required JAR files are present in the application directory

### Error Reporting
If you encounter issues:
1. Check the console output for error messages
2. Verify database connectivity and permissions
3. Ensure all required libraries are properly included

## Developer Information

### Project Structure
- `src/` - Source code directory
  - `inventory/management/system/` - Main package
    - Model classes (Product, User)
    - DAO classes (InventoryDAO, UserDAO)
    - GUI classes (InventoryGUI, ProductFormDialog, etc.)
    - Database utilities (DatabaseConnection)

### Dependencies
- mysql-connector-java-8.0.17.jar - MySQL JDBC driver
- rs2xml.jar - Library for ResultSet to TableModel conversion

### Building from Source
1. Clone the repository or download the source code
2. Set up required libraries in your project's classpath
3. Compile the source code
4. Package the application as a JAR file

## License

This software is provided for educational and demonstration purposes. All rights reserved.

## Credits

Developed as a Java desktop application project for inventory management solutions.