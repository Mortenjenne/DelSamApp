package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Post {
    private int postId;
    private String title;
    private String message;
    private Timestamp timestamp;
    private int upVotes;
    private int userId;

    //TODO Image image Category Enum?
}
