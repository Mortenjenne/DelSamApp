package app.services;

import app.entities.Post;
import app.exceptions.DatabaseException;
import app.persistence.PostMapper;

import java.util.Comparator;
import java.util.List;

public class PostServiceImpl implements PostService{

    private PostMapper postMapper;

    public PostServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    public Post createPost(String title, String message, String authorName, int userId) throws DatabaseException {
        validateTitle(title);
        validateMessage(message);

        return postMapper.createPost(title,message,authorName,userId);
    }

    @Override
    public Post getPostById(int postId) throws DatabaseException {
        Post post = postMapper.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Didn't find a post matching your search");
        }
        return post;
    }

    @Override
    public List<Post> getAllPosts() throws DatabaseException {
        List<Post> posts = postMapper.getAllPosts();
        posts.sort(Comparator.comparing(Post::getTimestamp).reversed());

        return List.copyOf(posts);
    }

    @Override
    public boolean updatePost(int postId, String title, String message) throws DatabaseException {
        validateTitle(title);
        validateMessage(message);

        return postMapper.updatePost(postId,title,message);
    }

    @Override
    public boolean upvotePost(int userId, int postId) throws DatabaseException {
        if(hasUserUpVotedPost(userId,postId)){
            throw new IllegalArgumentException("You have already upvoted this post!");
        }
        return postMapper.upvotePost(postId);
    }

    @Override
    public boolean delete(int postId) throws DatabaseException {
        return postMapper.delete(postId);
    }

    private boolean hasUserUpVotedPost(int userId, int postId) throws DatabaseException {
        return postMapper.hasUserUpVotedComment(userId,postId);
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
