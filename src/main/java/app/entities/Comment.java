package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Comment {
    private int commentId;
    private int postId;
    private int userId;
    private String content;
    private Timestamp timeStamp;
}
