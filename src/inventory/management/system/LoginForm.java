package retailinventory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Login form for user authentication
 */
public class LoginForm extends JDialog {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final UserDAO userDAO;
    private User authenticatedUser = null;
    
    /**
     * Creates login form
     */
    public LoginForm() {
        super((JFrame) null, "Login", true);
        userDAO = new UserDAO();
        
        setSize(350, 200);
        setLocationRelativeTo(null);
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
        
        add(formPanel, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");
        
        buttonsPanel.add(btnLogin);
        buttonsPanel.add(btnRegister);
        buttonsPanel.add(btnCancel);
        add(buttonsPanel, BorderLayout.SOUTH);
        
        // Set up button actions
        btnLogin.addActionListener(e -> {
            try {
                login();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnRegister.addActionListener(e -> {
            // Open registration form
            RegistrationForm regForm = new RegistrationForm(this);
            regForm.setVisible(true);
            
            // If registration was successful and user authenticated
            if (regForm.getAuthenticatedUser() != null) {
                this.authenticatedUser = regForm.getAuthenticatedUser();
                dispose();
            }
        });
        
        btnCancel.addActionListener(e -> dispose());
        
        // Set default button
        getRootPane().setDefaultButton(btnLogin);
    }
    
    /**
     * Attempt to login with entered credentials
     */
    private void login() throws SQLException {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = userDAO.authenticateUser(username, password);
        
        if (user != null) {
            authenticatedUser = user;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Returns authenticated user after successful login
     * @return User object if login successful, null otherwise
     */
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
    
    /**
     * Main method to test login form
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            
            User user = loginForm.getAuthenticatedUser();
            if (user != null) {
                JOptionPane.showMessageDialog(null,
                        "Login successful: " + user.getUsername(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Login cancelled.",
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
            
            System.exit(0);
        });
    }
}