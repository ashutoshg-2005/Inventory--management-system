package retailinventory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

/**
 * Dialog for adding or editing products
 */
public class ProductFormDialog extends JDialog {
    private final JTextField txtProductCode;
    private final JTextField txtProductName;
    private final JTextArea txtDescription;
    private final JTextField txtPrice;
    private final JSpinner spnQuantity;
    private final JSpinner spnReorderLevel;
    private final JTextField txtCategory;
    private final JTextField txtSupplier;
    
    private Product product;
    private boolean saveClicked = false;
    private final boolean isNewProduct;
    
    /**
     * Creates a new dialog for adding or editing a product
     * @param parent Parent frame
     * @param product Product to edit, or null for new product
     */
    public ProductFormDialog(JFrame parent, Product product) {
        super(parent, product == null ? "Add New Product" : "Edit Product", true);
        this.product = product;
        this.isNewProduct = (product == null);
        
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Create form panel with GridBagLayout for better control
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Product Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Product Code:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtProductCode = new JTextField(20);
        formPanel.add(txtProductCode, gbc);
        
        // Product Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Product Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtProductName = new JTextField(20);
        formPanel.add(txtProductName, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescription = new JTextArea(5, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDescription = new JScrollPane(txtDescription);
        formPanel.add(scrollDescription, gbc);
        
        // Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Price (₹):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPrice = new JTextField(10);
        formPanel.add(txtPrice, gbc);
        
        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Quantity:"), gbc);
        
        gbc.gridx = 1;
        spnQuantity = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        formPanel.add(spnQuantity, gbc);
        
        // Reorder Level
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Reorder Level:"), gbc);
        
        gbc.gridx = 1;
        spnReorderLevel = new JSpinner(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
        formPanel.add(spnReorderLevel, gbc);
        
        // Category
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        txtCategory = new JTextField(20);
        formPanel.add(txtCategory, gbc);
        
        // Supplier
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Supplier:"), gbc);
        
        gbc.gridx = 1;
        txtSupplier = new JTextField(20);
        formPanel.add(txtSupplier, gbc);
        
        // Add form panel to dialog
        add(formPanel, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        
        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnCancel);
        add(buttonsPanel, BorderLayout.SOUTH);
        
        // Set up button actions
        btnSave.addActionListener(e -> {
            if (validateForm()) {
                saveProduct();
                saveClicked = true;
                dispose();
            }
        });
        
        btnCancel.addActionListener(e -> dispose());
        
        // If editing existing product, populate form fields
        if (!isNewProduct) {
            populateForm();
            txtProductCode.setEditable(false); // Can't change product code for existing products
        }
    }
    
    /**
     * Populates form fields with existing product data
     */
    private void populateForm() {
        txtProductCode.setText(product.getProductCode());
        txtProductName.setText(product.getProductName());
        txtDescription.setText(product.getDescription());
        txtPrice.setText(product.getPrice().toString());
        spnQuantity.setValue(product.getQuantity());
        spnReorderLevel.setValue(product.getReorderLevel());
        txtCategory.setText(product.getCategory());
        txtSupplier.setText(product.getSupplier());
    }
    
    /**
     * Validates form input
     * @return true if valid, false otherwise
     */
    private boolean validateForm() {
        // Check required fields
        if (txtProductCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product Code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtProductCode.requestFocus();
            return false;
        }
        
        if (txtProductName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtProductName.requestFocus();
            return false;
        }
        
        // Validate price is a valid number
        try {
            new BigDecimal(txtPrice.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtPrice.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Creates or updates product from form data
     */
    private void saveProduct() {
        if (product == null) {
            product = new Product();
        }
        
        product.setProductCode(txtProductCode.getText().trim());
        product.setProductName(txtProductName.getText().trim());
        product.setDescription(txtDescription.getText().trim());
        product.setPrice(new BigDecimal(txtPrice.getText().trim()));
        product.setQuantity((Integer) spnQuantity.getValue());
        product.setReorderLevel((Integer) spnReorderLevel.getValue());
        product.setCategory(txtCategory.getText().trim());
        product.setSupplier(txtSupplier.getText().trim());
    }
    
    /**
     * Returns whether Save button was clicked
     * @return true if saved, false if canceled
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }
    
    /**
     * Returns the product that was created or edited
     * @return Product object
     */
    public Product getProduct() {
        return product;
    }
    
    /**
     * Returns whether this is a new product or editing existing
     * @return true if new product, false if editing
     */
    public boolean isNewProduct() {
        return isNewProduct;
    }
}