package app.services;

import app.entities.Comment;
import app.exceptions.DatabaseException;

import java.util.List;

public interface CommentService {
    public Comment createComment(int userId, int postId, String content) throws DatabaseException;
    public List<Comment> getAllCommentsInAPost(int postId) throws DatabaseException;
    public boolean updateComment(int commentId, String content) throws DatabaseException;
    public boolean upvoteComment(int commentId, int userId) throws DatabaseException;
    public void deleteComment(int commentId) throws DatabaseException;
}
