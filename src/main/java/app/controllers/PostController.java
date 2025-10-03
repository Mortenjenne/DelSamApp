package app.controllers;

import app.entities.Post;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.services.PostService;
import app.services.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService messageService, UserService userService) {
        this.postService = messageService;
        this.userService = userService;
    }

    public void addRoutes(Javalin app) {
        app.get("/messages", ctx -> showAllMessages(ctx));
        app.get("/createPost", ctx -> showCreatePostForm(ctx));
        app.post("/createPost", ctx -> createPost(ctx));
        app.post("/upvote/{id}", ctx -> upvotePost(ctx));
    }

    private void upvotePost(Context ctx) {
        User currentUser = ctx.sessionAttribute("currentUser");
        int postId = Integer.parseInt(ctx.pathParam("id"));

        try{
            if(postService.upvotePost(currentUser.getId(),postId)){
                ctx.attribute("errorUpvote","You have already upvoted!");
        }
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

        List<Post> messages = postService.getAllPosts();

        ctx.attribute("messages", messages);
        ctx.attribute("welcomemessage", "Welcome back " + currentUser.getUserName());
        ctx.render("msgboard");
    }

    private void createPost(Context ctx) throws DatabaseException {

        User currentUser = ctx.sessionAttribute("currentUser");
        String title = ctx.formParam("title");
        String body = ctx.formParam("body");

        Post post = postService.createPost(title,body,currentUser.getUserName(),currentUser.getId());

        ctx.redirect("/messages");
    }

    private void showCreatePostForm(Context ctx) {
        ctx.render("createPost.html");
    }
}

