package retailinventory;

import java.sql.Timestamp;

/**
 * User model class for authentication
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String gender; // Added gender field
    private String address; // Added address field
    private Timestamp createdAt;
    private Timestamp lastLogin;
    
    // Default constructor
    public User() {
    }
    
    // Constructor for new user registration
    public User(String username, String password, String fullName, String email, String gender, String address) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.address = address;
    }
    
    // Full constructor
    public User(int userId, String username, String password, String fullName, 
            String email, String gender, String address, Timestamp createdAt, Timestamp lastLogin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    @Override
    public String toString() {
        return username;
    }
}