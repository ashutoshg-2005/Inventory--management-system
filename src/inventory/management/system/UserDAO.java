package retailinventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for user authentication operations
 */
public class UserDAO {
    
    /**
     * Authenticates a user
     * @param username Username to authenticate
     * @param password Password to verify
     * @return User object if authentication successful, null otherwise
     * @throws SQLException if database error occurs
     */
    public User authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    return user;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Registers a new user
     * @param user User object with registration details
     * @return true if registration successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean registerUser(User user) throws SQLException {
        // Check if username already exists
        if (isUsernameExists(user.getUsername())) {
            return false;
        }
        
        String query = "INSERT INTO users (username, password, full_name, email, gender, address) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getGender());
            pstmt.setString(6, user.getAddress());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Checks if a username already exists in the database
     * @param username Username to check
     * @return true if username exists, false otherwise
     * @throws SQLException if database error occurs
     */
    private boolean isUsernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Maps a ResultSet to a User object
     * @param rs ResultSet containing user data
     * @return User object with data from ResultSet
     * @throws SQLException if database error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setGender(rs.getString("gender"));
        user.setAddress(rs.getString("address"));
        return user;
    }
}