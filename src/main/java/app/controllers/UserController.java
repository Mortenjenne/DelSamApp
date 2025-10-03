package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.services.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void addRoutes(Javalin app){
        app.get("/", ctx -> ctx.render("index"));
        app.post("/login", ctx -> login(ctx));
        app.get("/createUser", ctx -> ctx.render("createUser"));
        app.post("/createUser", ctx -> registerUser(ctx));
        app.get("/msgboard", ctx -> msgboard(ctx));
        app.get("/logout", ctx -> logout(ctx));
    }

    private void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    private void msgboard(Context ctx) {
        User currentUser = ctx.sessionAttribute("currentUser");

        if (currentUser == null) {
            ctx.redirect("/");
            return;
        }

        ctx.attribute("welcomemessage", "Welcome back " + currentUser.getUserName() + ". Good to have you back");
        ctx.render("msgboard");
    }

    private void registerUser(Context ctx) {
        try {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            String email = ctx.formParam("email");
            String role = "regularUser";

            User user = userService.registerUser(username, password, email, role);

            ctx.redirect("/");

        } catch (DatabaseException | IllegalArgumentException e) {
            ctx.attribute("errorMessage", "Could not create user: " + e.getMessage());
            ctx.render("createUser");
        }
    }

    private void login(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = userService.authenticateUser(username, password);

            if (user != null) {
                ctx.sessionAttribute("currentUser", user);
                ctx.render("/msgboard");
            } else {
                ctx.attribute("errorLogin", "Invalid username or password");
                ctx.render("index");
            }

        } catch (DatabaseException e) {
            ctx.attribute("errorLogin", e.getMessage());
            ctx.render("index");
        }
    }
}