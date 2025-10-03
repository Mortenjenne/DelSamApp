package app.services;

import app.entities.Post;
import app.exceptions.DatabaseException;

import java.util.List;

public interface PostService {
    public Post createPost(String title, String message, int userId) throws DatabaseException;
    public Post getPostById(int postId) throws DatabaseException;
    public List<Post> getAllPosts() throws DatabaseException;
    public boolean updatePost(int postId, String title, String message) throws DatabaseException;
    public boolean upvotePost(int userId, int postId) throws DatabaseException;
    public boolean delete(int postId) throws DatabaseException;

}
