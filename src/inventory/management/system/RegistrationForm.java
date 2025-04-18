package retailinventory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Registration form for new users
 */
public class RegistrationForm extends JDialog {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;
    private final JTextField txtFullName;
    private final JTextField txtEmail;
    private final JRadioButton rbMale;
    private final JRadioButton rbFemale;
    private final JTextArea txtAddress;
    private final UserDAO userDAO;
    private User authenticatedUser = null;
    
    /**
     * Creates registration form
     * @param parent Parent dialog
     */
    public RegistrationForm(JDialog parent) {
        super(parent, "Register New Account", true);
        userDAO = new UserDAO();
        
        setSize(450, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtUsername = new JTextField(15);
        formPanel.add(txtUsername, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(15);
        formPanel.add(txtPassword, gbc);
        
        // Confirm Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtConfirmPassword = new JPasswordField(15);
        formPanel.add(txtConfirmPassword, gbc);
        
        // Full Name field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtFullName = new JTextField(15);
        formPanel.add(txtFullName, gbc);
        
        // Email field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(15);
        formPanel.add(txtEmail, gbc);
        
        // Gender field (radio buttons)
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Gender:"), gbc);
        
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        
        // Group radio buttons to ensure only one can be selected
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        
        // Select male by default
        rbMale.setSelected(true);
        
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(genderPanel, gbc);
        
        // Address field (text area)
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtAddress = new JTextArea(4, 15);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane scrollAddress = new JScrollPane(txtAddress);
        formPanel.add(scrollAddress, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");
        
        buttonsPanel.add(btnRegister);
        buttonsPanel.add(btnCancel);
        add(buttonsPanel, BorderLayout.SOUTH);
        
        // Set up button actions
        btnRegister.addActionListener(e -> {
            try {
                register();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancel.addActionListener(e -> dispose());
        
        // Set default button
        getRootPane().setDefaultButton(btnRegister);
    }
    
    /**
     * Attempt to register with entered information
     */
    private void register() throws SQLException {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String gender = rbMale.isSelected() ? "Male" : "Female";
        String address = txtAddress.getText().trim();
        
        // Validate form
        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            txtUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Password cannot be empty.");
            txtPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            txtConfirmPassword.requestFocus();
            return;
        }
        
        // Create user and register
        User newUser = new User(username, password, fullName, email, gender, address);
        
        if (userDAO.registerUser(newUser)) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful!",
                    "Registration Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Auto-login the newly registered user
            authenticatedUser = userDAO.authenticateUser(username, password);
            dispose();
        } else {
            showError("Username already exists or registration failed.");
            txtUsername.requestFocus();
        }
    }
    
    /**
     * Shows an error message
     * @param message Error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Returns authenticated user after successful registration and auto-login
     * @return User object if registration successful, null otherwise
     */
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}