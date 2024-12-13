package org.bookexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CommentDto {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime timestamp;
    private List<CommentDto> replies;
    private Long parentCommentId;
    private String author;
    private int publicationId;
}