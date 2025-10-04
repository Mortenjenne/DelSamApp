package app.controllers;

import app.dto.CommentDTO;
import app.dto.PostAndCommentDTO;
import app.dto.PostDTO;
import app.entities.Post;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.services.CommentService;
import app.services.PostService;
import app.services.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    public PostController(PostService messageService, UserService userService, CommentService commentService) {
        this.postService = messageService;
        this.userService = userService;
        this.commentService = commentService;
    }

    public void addRoutes(Javalin app) {
        app.get("/messages", ctx -> showAllMessages(ctx));
        app.get("/createPost", ctx -> showCreatePostForm(ctx));
        app.get("/post/{id}", ctx -> showPost(ctx));

        app.post("/createPost", ctx -> createPost(ctx));
        app.post("/upvote/{id}", ctx -> upvotePost(ctx));
        app.post("/post/{id}/comment", ctx -> createComment(ctx));
    }

    private void createComment(Context ctx) {
        int postId = Integer.parseInt(ctx.pathParam("id"));
        User user = ctx.sessionAttribute("currentUser");
        String content = ctx.formParam("body");
        try {
            commentService.createComment(user.getId(),postId,content);
        } catch (DatabaseException e){
            ctx.attribute("errorMessage", e.getMessage());
        }
        ctx.redirect("/post/" + postId);

    }

    private void showPost(Context ctx) {
        int postId = Integer.parseInt(ctx.pathParam("id"));
        PostDTO postDTO = null;
        List<CommentDTO> commentDTOS = new ArrayList<>();
        try {
            postDTO = postService.getPostById(postId);
            commentDTOS = commentService.getAllCommentsInAPost(postId);
            PostAndCommentDTO postAndCommentDTO = new PostAndCommentDTO(postDTO, commentDTOS);

            ctx.attribute("postAndComment", postAndCommentDTO);
            ctx.render("post");

        } catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
            ctx.attribute("errorMessage", e.getMessage());
            ctx.redirect("/messages");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.redirect("/messages");
        }
    }

    private void upvotePost(Context ctx) {
        User currentUser = ctx.sessionAttribute("currentUser");
        int postId = Integer.parseInt(ctx.pathParam("id"));

        try{
            postService.upvotePost(currentUser.getId(),postId);
        }catch (DatabaseException | IllegalArgumentException e){
            ctx.attribute("errorUpvote", e.getMessage());
        }
        ctx.redirect("/messages");
    }

    private void showAllMessages(Context ctx) throws DatabaseException {
        User currentUser = ctx.sessionAttribute("currentUser");

        if (currentUser == null) {
            ctx.redirect("/");
            return;
        }

        List<PostDTO> messages = postService.getAllPosts();


        ctx.attribute("messages", messages);
        ctx.attribute("welcomemessage", "Velkommen tilbage " + currentUser.getUserName());
        ctx.render("msgboard");
    }

    private void createPost(Context ctx) throws DatabaseException {
        User currentUser = ctx.sessionAttribute("currentUser");
        String title = ctx.formParam("title");
        String body = ctx.formParam("body");

        byte[] imageData = null;
        UploadedFile uploadedFile = ctx.uploadedFile("image");

        if (uploadedFile != null) {
            if (uploadedFile.size() > 5 * 1024 * 1024) {
                ctx.attribute("errorMessage", "Billedet må max være 5MB");
                ctx.render("createPost.html");
                return;
            }

            String contentType = uploadedFile.contentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                ctx.attribute("errorMessage", "Kun billedfiler er tilladt");
                ctx.render("createPost.html");
                return;
            }

            try {
                imageData = uploadedFile.content().readAllBytes();
                uploadedFile.content().close();
            } catch (IOException e) {
                ctx.attribute("errorMessage", "Fejl ved læsning af billede");
                ctx.render("createPost.html");
                return;
            }
        }
        Post post = postService.createPost(title, body, currentUser.getUserName(), currentUser.getId(), imageData);
        ctx.redirect("/messages");
    }
    private void showCreatePostForm(Context ctx) {
        ctx.render("createPost.html");
    }
}

