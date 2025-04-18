package retailinventory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// Import rs2xml library
import net.proteanit.sql.DbUtils;

/**
 * Main GUI application for Inventory Management System
 */
public class InventoryGUI extends JFrame {
    
    private final InventoryDAO inventoryDAO;
    private HashMap<String, Product> inventory;
    
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterCategory;
    
    private User currentUser; // Current logged-in user
    private JLabel lblUserInfo; // Label to display current user info
    
    /**
     * Creates the main GUI for the Inventory Management System
     */
    public InventoryGUI() {
        super("Retail Shop Inventory Management System");
        
        inventoryDAO = new InventoryDAO();
        
        // Show login form before initializing the main GUI
        if (!showLoginForm()) {
            // If login was cancelled, exit the application
            System.exit(0);
        }
        
        setSize(1000, 600);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create UI components
        initComponents();
        
        // Load inventory data
        loadInventory();
        
        // Register window close event to close database connection
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection();
            }
        });
    }
    
    /**
     * Shows the login form and returns whether login was successful
     * @return true if login successful, false if cancelled
     */
    private boolean showLoginForm() {
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
        
        // Check if login was successful
        currentUser = loginForm.getAuthenticatedUser();
        
        if (currentUser != null) {
            // Set the user ID in the DAO for filtering
            inventoryDAO.setCurrentUserId(currentUser.getUserId());
            return true;
        }
        
        return false;
    }
    
    /**
     * Initializes UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Create search and filter panel
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search and filter panel (left)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JLabel lblSearch = new JLabel("Search: ");
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Search");
        
        JLabel lblCategory = new JLabel("Filter by Category: ");
        cmbFilterCategory = new JComboBox<>();
        cmbFilterCategory.addItem("All Categories");
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(lblCategory);
        searchPanel.add(cmbFilterCategory);
        
        // User info panel (right)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        lblUserInfo = new JLabel("Logged in as: " + currentUser.getUsername());
        JButton btnLogout = new JButton("Logout");
        
        userPanel.add(lblUserInfo);
        userPanel.add(btnLogout);
        
        // Add panels to top panel
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(userPanel, BorderLayout.EAST);
        
        // Create table
        String[] columnNames = {"Code", "Name", "Price", "Quantity", "Category", "Supplier", "Reorder Level", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 7 ? Boolean.class : Object.class;
            }
        };
        
        productsTable = new JTable(tableModel);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.setAutoCreateRowSorter(true);
        
        JScrollPane scrollPane = new JScrollPane(productsTable);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnAdd = new JButton("Add Product");
        JButton btnEdit = new JButton("Edit Product");
        JButton btnDelete = new JButton("Delete Product");
        JButton btnRefresh = new JButton("Refresh");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnRefresh);
        
        // Create quick update panel (for adjusting quantity)
        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        updatePanel.setBorder(BorderFactory.createTitledBorder("Quick Update Quantity"));
        
        JLabel lblUpdateQuantity = new JLabel("New Quantity:");
        JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        ((JSpinner.DefaultEditor) spnQuantity.getEditor()).getTextField().setColumns(4);
        JButton btnUpdateQuantity = new JButton("Update");
        
        updatePanel.add(lblUpdateQuantity);
        updatePanel.add(spnQuantity);
        updatePanel.add(btnUpdateQuantity);
        
        // Create a panel for buttons and quick update
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.add(buttonPanel, BorderLayout.WEST);
        actionPanel.add(updatePanel, BorderLayout.EAST);
        
        // Create summary panel (for stats)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Inventory Summary"));
        
        JLabel lblTotalItems = new JLabel("Total Products: 0");
        JLabel lblLowStock = new JLabel("Low Stock Items: 0");
        JLabel lblTotalValue = new JLabel("Total Inventory Value: ₹0.00");
        
        statsPanel.add(lblTotalItems);
        statsPanel.add(lblLowStock);
        statsPanel.add(lblTotalValue);
        
        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
        add(statsPanel, BorderLayout.EAST);
        
        // Register event handlers
        btnAdd.addActionListener(e -> showAddProductDialog());
        btnEdit.addActionListener(e -> showEditProductDialog());
        btnDelete.addActionListener(e -> deleteSelectedProduct());
        btnRefresh.addActionListener(e -> loadInventory());
        btnSearch.addActionListener(e -> searchProducts());
        btnUpdateQuantity.addActionListener(e -> updateProductQuantity((Integer) spnQuantity.getValue()));
        btnLogout.addActionListener(e -> logout());
        
        cmbFilterCategory.addActionListener(e -> filterByCategory());
        
        // Setup search on Enter key
        txtSearch.addActionListener(e -> searchProducts());
    }
    
    /**
     * Performs logout action
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Close current window
            
            // Start a new instance of the application
            SwingUtilities.invokeLater(() -> {
                InventoryGUI app = new InventoryGUI();
                app.setVisible(true);
            });
        }
    }
    
    /**
     * Loads all inventory data from database
     */
    private void loadInventory() {
        try {
            // Load inventory from database for other operations
            inventory = inventoryDAO.getAllProducts();
            
            // Update table with direct SQL query using rs2xml
            updateProductsTableWithRS2XML();
            
            // Populate categories dropdown
            updateCategoryFilter();
            
            // Update summary statistics
            updateInventorySummary();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                    "Error loading inventory data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Updates the products table using rs2xml library
     */
    private void updateProductsTableWithRS2XML() {
        try {
            String categoryFilter = (String) cmbFilterCategory.getSelectedItem();
            String searchText = txtSearch.getText().trim().toLowerCase();
            
            // Build the SQL query with appropriate filters
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT product_code as 'Code', product_name as 'Name', ");
            queryBuilder.append("CONCAT('₹', FORMAT(price, 2)) as 'Price', quantity as 'Quantity', ");
            queryBuilder.append("category as 'Category', supplier as 'Supplier', ");
            queryBuilder.append("reorder_level as 'Reorder Level', ");
            queryBuilder.append("CASE WHEN quantity <= reorder_level THEN 'Low' ELSE 'OK' END as 'Status' ");
            queryBuilder.append("FROM products WHERE 1=1 ");
            
            // Add user ID filter
            if (inventoryDAO.getCurrentUserId() > 0) {
                queryBuilder.append("AND user_id = ").append(inventoryDAO.getCurrentUserId()).append(" ");
            }
            
            // Add category filter
            if (categoryFilter != null && !"All Categories".equals(categoryFilter)) {
                queryBuilder.append("AND category = '").append(categoryFilter).append("' ");
            }
            
            // Add search filter
            if (searchText != null && !searchText.isEmpty()) {
                queryBuilder.append("AND (product_code LIKE '%").append(searchText).append("%' ");
                queryBuilder.append("OR product_name LIKE '%").append(searchText).append("%' ");
                queryBuilder.append("OR supplier LIKE '%").append(searchText).append("%') ");
            }
            
            queryBuilder.append("ORDER BY product_name");
            
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryBuilder.toString());
            
            // Use rs2xml to update the table model
            productsTable.setModel(DbUtils.resultSetToTableModel(rs));
            
            // Set column sizes and renderers again since the model has been replaced
            if (productsTable.getColumnCount() >= 8) {
                productsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Code
                productsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
                productsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Price
                productsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Quantity
                productsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Category
                productsTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Supplier
                productsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Reorder Level
                productsTable.getColumnModel().getColumn(7).setPreferredWidth(60);  // Status
                
                // Center align numeric columns
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                productsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Price
                productsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Quantity
                productsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Reorder Level
                
                // Custom cell renderer for low stock items
                productsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        
                        if (!isSelected) {
                            // Check the Status column to determine if stock is low
                            Object statusValue = table.getValueAt(row, 7); // Status column
                            if (statusValue != null && "Low".equals(statusValue.toString())) {
                                c.setBackground(new Color(255, 200, 200)); // Light red
                            } else {
                                c.setBackground(Color.WHITE);
                            }
                        }
                        
                        return c;
                    }
                });
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                    "Error updating products table: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Updates the category filter dropdown with available categories
     */
    private void updateCategoryFilter() {
        // Remember the current selection
        String selectedCategory = (String) cmbFilterCategory.getSelectedItem();
        
        // Clear and add "All Categories"
        cmbFilterCategory.removeAllItems();
        cmbFilterCategory.addItem("All Categories");
        
        // Get unique categories from inventory using HashMap for better efficiency
        HashMap<String, String> categoriesMap = new HashMap<>();
        for (Product product : inventory.values()) {
            String category = product.getCategory();
            if (category != null && !category.isEmpty()) {
                categoriesMap.put(category, category);
            }
        }
        
        // Sort categories alphabetically
        List<String> sortedCategories = new ArrayList<>(categoriesMap.keySet());
        sortedCategories.sort(String::compareToIgnoreCase);
        
        // Add categories to dropdown
        for (String category : sortedCategories) {
            cmbFilterCategory.addItem(category);
        }
        
        // Restore previous selection if it exists
        if (selectedCategory != null) {
            for (int i = 0; i < cmbFilterCategory.getItemCount(); i++) {
                if (selectedCategory.equals(cmbFilterCategory.getItemAt(i))) {
                    cmbFilterCategory.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    /**
     * Updates inventory summary statistics
     */
    private void updateInventorySummary() {
        int totalItems = inventory.size();
        int lowStockCount = 0;
        double totalValue = 0.0;
        
        // Calculate statistics
        for (Product product : inventory.values()) {
            if (product.isLowStock()) {
                lowStockCount++;
            }
            totalValue += product.getPrice().doubleValue() * product.getQuantity();
        }
        
        // Update labels with calculated statistics
        // These would be the labels defined in a summary panel
        Component[] components = ((JPanel)getContentPane().getComponent(3)).getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().startsWith("Total Products:")) {
                    label.setText("Total Products: " + totalItems);
                } else if (label.getText().startsWith("Low Stock Items:")) {
                    label.setText("Low Stock Items: " + lowStockCount);
                } else if (label.getText().startsWith("Total Inventory Value:")) {
                    DecimalFormat currencyFormat = new DecimalFormat("₹#,##0.00");
                    label.setText("Total Inventory Value: " + currencyFormat.format(totalValue));
                }
            }
        }
    }
    
    /**
     * Shows dialog to add a new product
     */
    private void showAddProductDialog() {
        ProductFormDialog dialog = new ProductFormDialog(this, null);
        dialog.setVisible(true);
        
        if (dialog.isSaveClicked()) {
            Product newProduct = dialog.getProduct();
            // Set the current user ID
            newProduct.setUserId(currentUser.getUserId());
            
            try {
                if (inventoryDAO.addProduct(newProduct)) {
                    JOptionPane.showMessageDialog(this,
                            "Product added successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadInventory(); // Refresh data
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add product.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Shows dialog to edit selected product
     */
    private void showEditProductDialog() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the product code from the selected row
        String productCode = (String) productsTable.getValueAt(selectedRow, 0);
        Product selectedProduct = inventory.get(productCode);
        
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this,
                    "Could not find the selected product in inventory.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ProductFormDialog dialog = new ProductFormDialog(this, selectedProduct);
        dialog.setVisible(true);
        
        if (dialog.isSaveClicked()) {
            Product updatedProduct = dialog.getProduct();
            // Maintain the same user ID
            updatedProduct.setUserId(selectedProduct.getUserId());
            
            try {
                if (inventoryDAO.updateProduct(updatedProduct)) {
                    JOptionPane.showMessageDialog(this,
                            "Product updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadInventory(); // Refresh data
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update product.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Deletes the selected product
     */
    private void deleteSelectedProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the product code from the selected row
        String productCode = (String) productsTable.getValueAt(selectedRow, 0);
        String productName = (String) productsTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the product: " + productName + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (inventoryDAO.deleteProduct(productCode)) {
                    JOptionPane.showMessageDialog(this,
                            "Product deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadInventory(); // Refresh data
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete product.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Updates quantity for selected product
     */
    private void updateProductQuantity(int newQuantity) {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the product code from the selected row
        String productCode = (String) productsTable.getValueAt(selectedRow, 0);
        
        try {
            if (inventoryDAO.updateProductQuantity(productCode, newQuantity)) {
                JOptionPane.showMessageDialog(this,
                        "Quantity updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInventory(); // Refresh data
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update quantity.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Searches products based on search text
     */
    private void searchProducts() {
        updateProductsTableWithRS2XML();
    }
    
    /**
     * Filters products by selected category
     */
    private void filterByCategory() {
        updateProductsTableWithRS2XML();
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        try {
            // Set System Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            InventoryGUI app = new InventoryGUI();
            app.setVisible(true);
        });
    }
}