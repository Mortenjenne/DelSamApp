package app.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Data
public class PostDTO {
    private int postId;
    private String title;
    private String message;
    private String authorName;
    private Timestamp createdAt;
    private byte[] image;
    private int upvoteCount;
    private int commentCount;
    private String imageBase64;

    public PostDTO(int postId, String title, String message, String authorName,
                   Timestamp createdAt, byte[] image, int upvoteCount, int commentCount) {
        this.postId = postId;
        this.title = title;
        this.message = message;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.image = image;
        this.upvoteCount = upvoteCount;
        this.commentCount = commentCount;

        convertImage();
    }

    private void convertImage() {
        if (image != null && image.length > 0) {
            this.imageBase64 = Base64.getEncoder().encodeToString(image);
        }
    }

    public boolean hasImage() {
        return imageBase64 != null && !imageBase64.isEmpty();
    }

    public String getRelativeTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime postTime = createdAt.toLocalDateTime();
        Duration duration = Duration.between(postTime, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) return "Lige nu";
        if (minutes < 60) return minutes + " min siden";
        if (hours < 24) return hours + " timer siden";
        if (days < 7) return days + " dage siden";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return postTime.format(formatter);
    }
}