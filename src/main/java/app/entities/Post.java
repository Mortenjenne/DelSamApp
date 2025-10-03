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

    public String getRelativeTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime postTime = timestamp.toLocalDateTime();
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
