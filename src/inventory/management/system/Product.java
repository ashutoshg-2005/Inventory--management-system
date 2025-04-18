package retailinventory;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product model class representing inventory items
 */
public class Product {
    private int productId;
    private String productCode;
    private String productName;
    private String description;
    private BigDecimal price;
    private int quantity;
    private int reorderLevel;
    private String category;
    private String supplier;
    private Timestamp lastUpdated;
    private int userId; // Added to track which user owns this product
    
    // Default constructor
    public Product() {
    }
    
    // Constructor with essential fields
    public Product(String productCode, String productName, BigDecimal price, int quantity) {
        this.productCode = productCode;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.reorderLevel = 10; // Default reorder level
    }
    
    // Full constructor
    public Product(int productId, String productCode, String productName, 
            String description, BigDecimal price, int quantity, 
            int reorderLevel, String category, String supplier, Timestamp lastUpdated, int userId) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.category = category;
        this.supplier = supplier;
        this.lastUpdated = lastUpdated;
        this.userId = userId;
    }
    
    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    // Method to check if stock is low
    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }
    
    @Override
    public String toString() {
        return productName + " (" + productCode + ")";
    }
}