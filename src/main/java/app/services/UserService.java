package app.services;

import app.entities.User;
import app.exceptions.DatabaseException;

public interface UserService {
    public User getUserById(int userId) throws DatabaseException;
    public boolean deleteUser(int userId) throws DatabaseException;
    public User registerUser(String userName, String password, String email, String role) throws DatabaseException;
    public User authenticateUser(String email, String password) throws DatabaseException;
    public boolean updateUser(User user) throws DatabaseException;
}
