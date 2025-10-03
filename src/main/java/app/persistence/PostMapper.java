package app.persistence;

import app.entities.Post;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostMapper {

    public Post createPost(String title, String message, int userId) throws DatabaseException {
        String sql = "INSERT INTO post (title, message, user_id, upvotes) VALUES (?,?,?,?)";
        Post post = null;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, title);
            ps.setString(2, message);
            ps.setInt(3, userId);
            ps.setInt(4, 0);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                int postId = rs.getInt(1);
                Timestamp timeStamp = rs.getTimestamp("created_at");
                post = new Post(postId, title, message, timeStamp, 0,userId);

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

        String sql = "select * from post where post_id = ?";

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
                Timestamp timeStamp = rs.getTimestamp("created_at");
                int upvotes = rs.getInt("upvotes");
                int userId = rs.getInt("user_id");

                post = new Post(postId, title, message, timeStamp, upvotes, userId);
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
        String sql = "select * from post";

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
                int userId = rs.getInt("user_id");
                Timestamp timeStamp = rs.getTimestamp("created_at");
                int upvotes = rs.getInt("upvotes");
                posts.add(new Post(postId, title, message, timeStamp, upvotes,userId));
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

    public boolean upvotePost(int postId) throws DatabaseException {
        String sql = "UPDATE post SET upvotes = upvotes + 1 WHERE post_id = ?";
        boolean result = false;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, postId);
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

    public boolean hasUserUpVotedComment(int user_id, int post_id) throws DatabaseException {
        String sql = "SELECT user_id, post_id FROM post WHERE user_id = ? AND post_id = ?";
        boolean result = false;

        try(Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,user_id);
            ps.setInt(2,post_id);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = true;
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
