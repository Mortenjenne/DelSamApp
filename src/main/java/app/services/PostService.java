package app.services;

import app.dto.PostDTO;
import app.entities.Post;
import app.exceptions.DatabaseException;

import java.util.List;

public interface PostService {
    public Post createPost(String title, String message, String authorName, int userId) throws DatabaseException;
    public PostDTO getPostById(int postId) throws DatabaseException;
    public List<PostDTO> getAllPosts() throws DatabaseException;
    public boolean updatePost(int postId, String title, String message) throws DatabaseException;
    public void upvotePost(int userId, int postId) throws DatabaseException;
    public boolean delete(int postId) throws DatabaseException;

}
