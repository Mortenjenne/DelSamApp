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
        app.get("/", ctx -> root(ctx));
        app.post("/login", ctx -> login(ctx));
        app.get("/createUser", ctx -> ctx.render("createUser"));
        app.post("/createUser", ctx -> registerUser(ctx));
        app.get("/logout", ctx -> logout(ctx));
    }

    private void root(Context ctx) {
        String successMessage = ctx.sessionAttribute("successMessage");
        if (successMessage != null) {
            ctx.attribute("message", successMessage);
            ctx.sessionAttribute("successMessage", null);
        }
        ctx.render("index");
    }

    private void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    private void registerUser(Context ctx) {
        String username = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String email = ctx.formParam("email");
        String role = "regularUser";

        try {
            if (password1.equals(password2)) {
                User user = userService.registerUser(username, password1, email, role);
                ctx.sessionAttribute("successMessage", "Din bruger blev oprettet, nu kan du logge ind.");

                ctx.redirect("/");
            } else {
                ctx.attribute("errorPassword", "Passwords er ikke identiske, prøv igen.");
                ctx.attribute("usernameValue", username);
                ctx.attribute("emailValue", email);
                ctx.render("createUser");
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Username")) {
                ctx.attribute("errorUserName", e.getMessage());
            } else if (e.getMessage().contains("Email")) {
                ctx.attribute("errorEmail", e.getMessage());
            } else if (e.getMessage().contains("Password")) {
                ctx.attribute("errorPassword", e.getMessage());
            }
            ctx.attribute("usernameValue", username);
            ctx.attribute("emailValue", email);
            ctx.render("createUser");

        } catch (DatabaseException e) {
            ctx.attribute("errorEmail", e.getMessage());
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
                ctx.redirect("/messages");
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