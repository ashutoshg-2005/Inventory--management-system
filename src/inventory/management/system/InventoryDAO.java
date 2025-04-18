package retailinventory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Data Access Object for inventory operations
 */
public class InventoryDAO {
    
    private int currentUserId = 0; // Default to 0 for no user
    
    /**
     * Sets the current user ID for filtering products
     * @param userId User ID to filter by
     */
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
    
    /**
     * Gets the current user ID
     * @return Current user ID
     */
    public int getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Gets all products for the current user
     * @return HashMap of products with product code as key
     * @throws SQLException if database error occurs
     */
    public HashMap<String, Product> getAllProducts() throws SQLException {
        HashMap<String, Product> productMap = new HashMap<>();
        
        // Include user ID filter if a user is set
        String query = "SELECT * FROM products WHERE 1=1";
        if (currentUserId > 0) {
            query += " AND user_id = ?";
        }
        query += " ORDER BY product_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Set user ID parameter if filtering by user
            if (currentUserId > 0) {
                pstmt.setInt(1, currentUserId);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    productMap.put(product.getProductCode(), product);
                }
            }
        }
        
        return productMap;
    }
    
    /**
     * Retrieves a single product by product code
     * @param productCode The code of the product to retrieve
     * @return Product object if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public Product getProductByCode(String productCode) throws SQLException {
        String query = "SELECT * FROM products WHERE product_code = ?";
        if (currentUserId > 0) {
            query += " AND user_id = ?";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, productCode);
            if (currentUserId > 0) {
                pstmt.setInt(2, currentUserId);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Adds a new product to the database
     * @param product The product to add
     * @return true if successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (product_code, product_name, description, price, " +
                "quantity, reorder_level, category, supplier, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, product.getProductCode());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getDescription());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setInt(5, product.getQuantity());
            pstmt.setInt(6, product.getReorderLevel());
            pstmt.setString(7, product.getCategory());
            pstmt.setString(8, product.getSupplier());
            pstmt.setInt(9, currentUserId > 0 ? currentUserId : 1); // Default to user_id=1 if no user is set
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Updates an existing product in the database
     * @param product The product to update
     * @return true if successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET product_name = ?, description = ?, price = ?, " +
                "quantity = ?, reorder_level = ?, category = ?, supplier = ? " +
                "WHERE product_code = ?";
        
        // Add user ID filter if a user is set
        if (currentUserId > 0) {
            query += " AND user_id = ?";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, product.getProductName());
            pstmt.setString(2, product.getDescription());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getQuantity());
            pstmt.setInt(5, product.getReorderLevel());
            pstmt.setString(6, product.getCategory());
            pstmt.setString(7, product.getSupplier());
            pstmt.setString(8, product.getProductCode());
            
            if (currentUserId > 0) {
                pstmt.setInt(9, currentUserId);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Updates only the quantity of a product
     * @param productCode The product code to update
     * @param newQuantity The new quantity value
     * @return true if successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean updateProductQuantity(String productCode, int newQuantity) throws SQLException {
        String query = "UPDATE products SET quantity = ? WHERE product_code = ?";
        
        // Add user ID filter if a user is set
        if (currentUserId > 0) {
            query += " AND user_id = ?";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, productCode);
            
            if (currentUserId > 0) {
                pstmt.setInt(3, currentUserId);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Deletes a product from the database
     * @param productCode The product code to delete
     * @return true if successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean deleteProduct(String productCode) throws SQLException {
        String query = "DELETE FROM products WHERE product_code = ?";
        
        // Add user ID filter if a user is set
        if (currentUserId > 0) {
            query += " AND user_id = ?";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, productCode);
            
            if (currentUserId > 0) {
                pstmt.setInt(2, currentUserId);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Gets a list of products with quantity below or equal to reorder level
     * @return HashMap of products needing restocking with product code as key
     * @throws SQLException if database error occurs
     */
    public HashMap<String, Product> getLowStockProducts() throws SQLException {
        HashMap<String, Product> lowStockProducts = new HashMap<>();
        String query = "SELECT * FROM products WHERE quantity <= reorder_level";
        
        // Add user ID filter if a user is set
        if (currentUserId > 0) {
            query += " AND user_id = ?";
        }
        
        query += " ORDER BY product_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (currentUserId > 0) {
                pstmt.setInt(1, currentUserId);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    lowStockProducts.put(product.getProductCode(), product);
                }
            }
        }
        
        return lowStockProducts;
    }
    
    /**
     * Helper method to map ResultSet to Product object
     * @param rs ResultSet from database query
     * @return Populated Product object
     * @throws SQLException if database error occurs
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductCode(rs.getString("product_code"));
        product.setProductName(rs.getString("product_name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setQuantity(rs.getInt("quantity"));
        product.setReorderLevel(rs.getInt("reorder_level"));
        product.setCategory(rs.getString("category"));
        product.setSupplier(rs.getString("supplier"));
        product.setLastUpdated(rs.getTimestamp("last_updated"));
        product.setUserId(rs.getInt("user_id")); // Add user ID to product
        return product;
    }
}