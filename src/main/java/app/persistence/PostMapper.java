package app.persistence;

import app.entities.Post;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostMapper {

    public Post createPost(String title, String message, String authorName, int userId) throws DatabaseException {
        String sql = "INSERT INTO post (title, message, user_id) VALUES (?,?,?)";
        Post post = null;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, title);
            ps.setString(2, message);
            ps.setInt(3, userId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                int postId = rs.getInt(1);
                Timestamp timeStamp = rs.getTimestamp("created_at");
                post = new Post(postId, title, message, authorName, timeStamp,userId);

            } else {
                throw new DatabaseException("Fejl under indsætning af post: " + title);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Couldn't create post");
        }
        return post;
    }

    public Post getPostById(int postId) throws DatabaseException
    {
        Post post = null;

        String sql = "select * from post p join users u on u.user_id = p.user_id WHERE p.post_id =?";

        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                String title = rs.getString("title");
                String message = rs.getString("message");
                String authorName = rs.getString("username");
                Timestamp timeStamp = rs.getTimestamp("created_at");
                int userId = rs.getInt("user_id");

                post = new Post(postId, title, message, authorName, timeStamp, userId);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl ved hentning af post med id = " + postId + e.getMessage());
        }
        return post;
    }

    public List<Post> getAllPosts() throws DatabaseException
    {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.post_id, p.title, p.message, u.username AS author_name, p.created_at, p.user_id\n" +
                "FROM post p\n" +
                "JOIN users u ON u.user_id = p.user_id;";

        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {

            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int postId = rs.getInt("post_id");
                String title = rs.getString("title");
                String message = rs.getString("message");
                String authorName = rs.getString("author_name");
                int userId = rs.getInt("user_id");
                Timestamp timeStamp = rs.getTimestamp("created_at");
                posts.add(new Post(postId, title, message, authorName, timeStamp,userId));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl ved hentning af alle filer!!!!" + e.getMessage());
        }
        return posts;
    }

    public boolean updatePost(int postId, String title, String message) throws DatabaseException {

        String sql = "UPDATE post SET title = ?, message = ? WHERE post_id = ?";
        boolean result = false;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, message);
            ps.setInt(3, postId);

            int rowsAffected = ps.executeUpdate();

            if(rowsAffected == 1){
                result = true;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Couldn't update post: " + e.getMessage());
        }
        return result;
    }

    public int getTotalPostUpvoteCount(int postId) throws DatabaseException {
        String sql = "SELECT COUNT(*) AS total \n" +
                "FROM post_upvotes \n" +
                "WHERE post_id = ?";
        int result = 0;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                result = rs.getInt("total");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Couldn't get total upvotes " + e.getMessage());
        }

        return result;
    }

    public boolean upvotePost(int userId, int postId) throws DatabaseException {
        String sql = "INSERT INTO post_upvotes (user_id, post_id) \n" +
                "VALUES (?, ?)";
        boolean result = false;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2,postId);
            int rowsAffected = ps.executeUpdate();

            if(rowsAffected == 1){
                result = true;
            } else {
                throw new DatabaseException("No post found with id: " + postId);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Couldn't upvote post: " + e.getMessage());
        }

        return result;
    }

    public boolean deleteUserUpVote(int user_id, int post_id) throws DatabaseException {
        String sql = "DELETE FROM post_upvotes \n" +
                "WHERE user_id = ? AND post_id = ?";
        boolean result = false;

        try(Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,user_id);
            ps.setInt(2,post_id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1)
            {
                result = true;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af kommentare vha. userId og postId");
        }
        return result;
    }

    public boolean hasUserUpVotedPost(int user_id, int post_id) throws DatabaseException {
        String sql = "SELECT COUNT(*) \n" +
                "FROM post_upvotes \n" +
                "WHERE user_id = ? AND post_id = ?";
        boolean result = false;

        try(Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,user_id);
            ps.setInt(2,post_id);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af kommentare vha. userId og postId");
        }
        return result;
    }

    public boolean delete(int postId) throws DatabaseException
    {
        String sql = "delete from post where post_id = ?";
        boolean result = false;

        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setInt(1, postId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl ved sletning af en post" + e.getMessage());
        }
        return result;
    }

}
