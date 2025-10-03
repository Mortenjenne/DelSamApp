package app.services;

import app.entities.Comment;
import app.exceptions.DatabaseException;
import app.persistence.CommentMapper;

import java.util.List;

public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public Comment createComment(int userId, int postId, String content) throws DatabaseException {

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }


        if (content.length() > 500) {
            throw new IllegalArgumentException("Comment is too long (max 500 characters)");
        }

        return commentMapper.createComment(userId, postId, content);
    }

    @Override
    public List<Comment> getAllCommentsInAPost(int postId) throws DatabaseException {
        List<Comment> comments = commentMapper.getAllCommentsInAPost(postId);
        comments.sort((c1, c2) -> c2.getTimeStamp().compareTo(c1.getTimeStamp()));

        return List.copyOf(comments);
    }

    @Override
    public boolean updateComment(int commentId, String content) throws DatabaseException {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Updated comment cannot be empty");
        }
        return commentMapper.updateComment(commentId, content);
    }

    @Override
    public boolean upvoteComment(int userId, int commentId) throws DatabaseException {
        if(hasUserUpVotedComment(userId, commentId)){
            throw new IllegalArgumentException("You have already upvoted this comment!");
        }
        return commentMapper.upvoteComment(commentId);
    }

    @Override
    public void deleteComment(int commentId) throws DatabaseException {
        // TODO kun comment-ejeren eller en admin må slette
        commentMapper.deleteComment(commentId);
    }

    private boolean hasUserUpVotedComment(int userId, int commentId) throws DatabaseException {
        return commentMapper.hasUserUpVotedComment(userId,commentId);
    }
}
