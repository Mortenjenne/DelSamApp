package app;

import app.config.ThymeleafConfig;
import app.controllers.PostController;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import app.persistence.PostMapper;
import app.persistence.UserMapper;
import app.services.PostService;
import app.services.PostServiceImpl;
import app.services.UserServiceImpl;
import app.services.UserService;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "bulletinApp";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            config.staticFiles.add("/templates");
        }).start(7075);

        UserMapper userMapper = new UserMapper(connectionPool);
        UserService userService = new UserServiceImpl(userMapper);
        UserController userController = new UserController(userService);
        userController.addRoutes(app);

        PostMapper postMapper = new PostMapper();
        PostService postService = new PostServiceImpl(postMapper);
        PostController postController = new PostController(postService,userService);
        postController.addRoutes(app);

    }

}