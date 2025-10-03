package app.services;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.UserMapper;

import java.security.MessageDigest;
import java.util.Base64;

public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUserById(int userId) throws DatabaseException {
        validateUserId(userId);
        return userMapper.getUserById(userId);
    }

    @Override
    public boolean deleteUser(int userId) throws DatabaseException {
        validateUserId(userId);
        return userMapper.deleteMember(userId);
    }

    @Override
    public User registerUser(String username, String password, String email, String role) throws DatabaseException {
        //validateUsername(username);
        //validateEmail(email);
        //validatePassword(password);


        //String hashedPassword = hashPassword(password);


        return userMapper.createUser(username, password, email, role);
    }

    @Override
    public User authenticateUser(String email, String password) throws DatabaseException {
        // Hash password før sammenligning
        //String hashedPassword = hashPassword(password);
        return userMapper.login(email, password);
    }

    @Override
    public boolean updateUser(User user) throws DatabaseException {
        return userMapper.updateUser(user);
    }

    // --- VALIDATION HELPERS ---

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }

    private void validateEmail(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (userMapper.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain uppercase letter");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contain a number");
        }
    }

    private String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(plainPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}
