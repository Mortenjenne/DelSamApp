package app.controllers;

import app.entities.Post;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.services.PostService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class PostController {
    private final PostService postService;

    public PostController(PostService messageService) {
        this.postService = messageService;
    }

    public void addRoutes(Javalin app) {
        app.get("/messages", ctx -> showAllMessages(ctx));
        app.get("/createPost", ctx -> showCreatePostForm(ctx));
        app.post("/createPost", ctx -> createPost(ctx));
    }

    private void showAllMessages(Context ctx) throws DatabaseException {
        List<Post> messages = postService.getAllPosts();
        ctx.attribute("messages", messages);
        ctx.render("messageBoard.html");
    }

    private void createPost(Context ctx) throws DatabaseException {

        User currentUser = ctx.sessionAttribute("currentUser");
        String title = ctx.formParam("title");
        String body = ctx.formParam("body");

        Post post = postService.createPost(title,body,currentUser.getId());

        ctx.redirect("/msgboard");
    }

    private void showCreatePostForm(Context ctx) {
        ctx.render("createPost.html"); // viser formularen
    }
}

