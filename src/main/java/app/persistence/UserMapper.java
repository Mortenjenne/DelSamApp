package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.*;

    public class UserMapper {

        private ConnectionPool connectionPool;

        public UserMapper(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
        }

        public User login(String username, String password) throws DatabaseException {
            String sql = "SELECT user_id, username, password, email, role " +
                    "FROM users WHERE username = ? AND password = ?";
            User user = null;

            try(Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1,username);
                ps.setString(2,password);

                ResultSet rs = ps.executeQuery();

                if(rs.next()){
                    int newId = rs.getInt(1);
                    user = new User(newId,username,password);
                }

            } catch (SQLException e) {
                throw new DatabaseException("Couldnt find user");
            }
            return user;
        }

        public User getUserById(int userId) throws DatabaseException {
            User user = null;
            String sql = "SELECT user_id, username, password, email, role FROM users WHERE user_id = ?";  // account i stedet for users

            try (Connection connection = connectionPool.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String userName = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    String role = rs.getString("role");

                    user = new User(id, userName, password, email, role);
                }

            } catch (SQLException e) {
                throw new DatabaseException("Fejl ved hentning af bruger med id " + userId + ": " + e.getMessage(), e);
            }

            return user;
        }

        public boolean deleteMember(int user_id) throws DatabaseException {
            boolean result = false;
            String sql = "DELETE FROM users WHERE user_id = ?";
            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setLong(1, user_id);
                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected == 1){
                        result = true;
                    }
                } catch (SQLException e) {
                    throw new DatabaseException("Fejl ved hentning af alle medlemmer: " + e.getMessage(), e);
                }
            } catch (SQLException e) {
                throw new DatabaseException("Kunne ikke oprette forbindelse til databasen: " + e.getMessage(), e);
            }
            return result;
        }

        public User createUser(String username, String password, String mail, String role) throws DatabaseException {
            boolean result = false;
            int newId = 0;
            User user = null;
            String sql = "insert into users (username, password, email, role) values (?,?,?,?)";
            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, mail);
                    ps.setString(4, role);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected == 1){
                        result = true;
                    }
                    ResultSet idResultset = ps.getGeneratedKeys();
                    if (idResultset.next()){
                        newId = idResultset.getInt(1);
                        user = new User(newId,username,password,mail,role);
                    }

                } catch (SQLException e) {
                    throw new DatabaseException("Fejl ved hentning af alle medlemmer: " + e.getMessage(), e);
                }
            } catch (SQLException e) {
                throw new DatabaseException("Kunne ikke oprette forbindelse til databasen: " + e.getMessage(), e);
            }
            return user;
        }

        public boolean updateUser(User user) throws DatabaseException {
            boolean result = false;
            String sql = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE user_id = ?";

            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, user.getUserName());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getEmail());
                    ps.setString(4, user.getRole());
                    ps.setLong(5, user.getId());

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected == 1){
                        result = true;
                    }
                } catch (SQLException e) {
                    throw new DatabaseException("Fejl ved hentning af alle medlemmer: " + e.getMessage(), e);
                }
            } catch (SQLException e) {
                throw new DatabaseException("Kunne ikke oprette forbindelse til databasen: " + e.getMessage(), e);
            }
            return result;
        }



        public boolean emailExists(String email) throws DatabaseException {
            boolean result = false;
            String sql = "SELECT FROM user WHERE email = ?";
            try(Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1){
                    result = true;
                }
            }catch (SQLException e){
                throw new DatabaseException("Fejl ved hentning af email");
            }
            return result;
        }
    }

