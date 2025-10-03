package app.services;

import app.dto.CommentDTO;
import app.entities.Comment;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.CommentMapper;
import app.persistence.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public CommentServiceImpl(CommentMapper commentMapper, UserMapper userMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
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
    public List<CommentDTO> getAllCommentsInAPost(int postId) throws DatabaseException {
        List<Comment> comments = commentMapper.getAllCommentsInAPost(postId);
        List<CommentDTO> commentDTOS = new ArrayList<>();

        for(Comment comment: comments){
            User user = userMapper.getUserById(comment.getUserId());
            commentDTOS.add(new CommentDTO(comment.getCommentId(),comment.getContent(),user.getUserName(),comment.getTimeStamp()));
        }
        commentDTOS.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        return List.copyOf(commentDTOS);
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
