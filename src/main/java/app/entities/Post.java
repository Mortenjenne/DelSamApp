package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class Post {
    private int postId;
    private String title;
    private String message;
    private String authorName;
    private Timestamp timestamp;
    private int userId;

    //TODO Image image Category Enum?


}
