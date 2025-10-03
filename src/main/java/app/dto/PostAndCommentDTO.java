package app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@AllArgsConstructor
@Data
public class PostAndCommentDTO {
    PostDTO postDTO;
    List<CommentDTO> commentDTOList;
}
