package app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class CommentDTO {
    private int commentId;
    private String content;
    private String authorName;
    private Timestamp createdAt;


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