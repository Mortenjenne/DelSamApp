package app.persistence;

import app.entities.Comment;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public Comment createComment(int userId, int postId, String content) throws DatabaseException {
        String sql = "INSERT INTO comment (user_id, post_id, content) VALUES (?,?,?)";
        Comment comment = null;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setInt(2, postId);
            ps.setString(3, content);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                int commentId = rs.getInt(1);
                Timestamp timeStamp = rs.getTimestamp("created_at");
                comment = new Comment(commentId,postId,userId,content,timeStamp,0);

            } else {
                throw new DatabaseException("Fejl under oprettelse af en kommentar: ");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Couldn't create post");
        }
        return comment;
    }

    public boolean hasUserUpVotedComment(int user_id, int comment_id) throws DatabaseException {
        String sql = "SELECT user_id, comment_id FROM comment WHERE user_id = ? AND comment_id = ?";
        boolean result = false;

        try(Connection connection = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,user_id);
            ps.setInt(2,comment_id);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = true;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af kommentare vha. userId og commentId");
        }
        return result;
    }

    public List<Comment> getAllCommentsInAPost(int postId) throws DatabaseException
    {
        List<Comment> result = new ArrayList<>();

        String sql = "select * from comment where post_id = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int commentId = rs.getInt("comment_id");
                int userId = rs.getInt("user_id");
                String content = rs.getString("message");
                Timestamp timeStamp = rs.getTimestamp("created_at");
                int upvotes = rs.getInt("upvotes");

                result.add(new Comment(commentId,postId,userId,content,timeStamp,upvotes));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl ved hentning af alle filer!!!!" + e.getMessage());
        }
        return result;
    }


    public boolean updateComment(int commentId, String content) throws DatabaseException {

        String sql = "UPDATE comment SET content = ? WHERE comment_id = ?";
        boolean result = false;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, content);
            ps.setInt(2, commentId);


            int rowsAffected = ps.executeUpdate();

            if(rowsAffected == 1){
                result = true;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Couldn't update comment: " + e.getMessage());
        }
        return result;
    }

    public boolean upvoteComment(int commentId) throws DatabaseException {
        String sql = "UPDATE comment SET upvotes = upvotes + 1 WHERE comment_id = ?";
        boolean result = false;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            int rowsAffected = ps.executeUpdate();

            if(rowsAffected == 1){
                result = true;

            } else {
                throw new DatabaseException("No comment found with id: " + commentId);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Couldn't upvote comment: " + e.getMessage());
        }

        return result;
    }

    public void deleteComment(int commentId) throws DatabaseException
    {
        String sql = "delete from comment where comment_id = ?";

        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setInt(1, commentId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl i sletning af en comment");
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl ved sletning af en comment" + e.getMessage());
        }
    }

}
