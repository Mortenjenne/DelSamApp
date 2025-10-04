package app.services;

import app.dto.PostDTO;
import app.entities.Post;
import app.exceptions.DatabaseException;
import app.persistence.CommentMapper;
import app.persistence.PostMapper;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PostServiceImpl implements PostService{

    private PostMapper postMapper;
    private CommentMapper commentMapper;

    public PostServiceImpl(PostMapper postMapper, CommentMapper commentMapper) {
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public Post createPost(String title, String message, String authorName, int userId, byte[] image) throws DatabaseException {
        validateTitle(title);
        validateMessage(message);

        return postMapper.createPost(title,message,authorName,userId,image);


    }

    @Override
    public PostDTO getPostById(int postId) throws DatabaseException {
        Post post = postMapper.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Didn't find a post matching your search");
        }
        int upVotes = postMapper.getTotalPostUpvoteCount(postId);
        int commentCount = commentMapper.getCommentCountByPostId(postId);
        PostDTO postDTO = new PostDTO(
                postId,
                post.getTitle(),
                post.getMessage(),
                post.getAuthorName(),
                post.getTimestamp(),
                post.getImage(),
                upVotes,
                commentCount
        );
        return postDTO;
    }

    @Override
    public List<PostDTO> getAllPosts() throws DatabaseException {
        List<Post> posts = postMapper.getAllPosts();
        List<PostDTO> postDTOS = new ArrayList<>();

        if(!posts.isEmpty()) {
            for (Post post : posts) {
                int upVotes = postMapper.getTotalPostUpvoteCount(post.getPostId());
                int commentCount = commentMapper.getCommentCountByPostId(post.getPostId());
                PostDTO postDTO = new PostDTO(
                        post.getPostId(),
                        post.getTitle(),
                        post.getMessage(),
                        post.getAuthorName(),
                        post.getTimestamp(),
                        post.getImage(),
                        upVotes,
                        commentCount
                );
                postDTOS.add(postDTO);
            }
            postDTOS.sort(Comparator.comparing(PostDTO::getCreatedAt).reversed());
        }
        return List.copyOf(postDTOS);
    }

    @Override
    public boolean updatePost(int postId, String title, String message) throws DatabaseException {
        validateTitle(title);
        validateMessage(message);

        return postMapper.updatePost(postId,title,message);
    }

    @Override
    public void upvotePost(int userId, int postId) throws DatabaseException {
        if(hasUserUpVotedPost(userId,postId)){
            postMapper.deleteUserUpVote(userId,postId);
        } else {
            postMapper.upvotePost(userId,postId);
        }
    }

    @Override
    public boolean delete(int postId) throws DatabaseException {
        return postMapper.delete(postId);
    }

    private boolean hasUserUpVotedPost(int userId, int postId) throws DatabaseException {
        return postMapper.hasUserUpVotedPost(userId,postId);
    }

    private void validateTitle(String title){
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }


        if (title.length() > 50) {
            throw new IllegalArgumentException("Title is too long (max 50 characters)");
        }
    }

    private void validateMessage(String message){
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }


        if (message.length() > 500) {
            throw new IllegalArgumentException("Message is too long (max 500 characters)");
        }
    }
}
