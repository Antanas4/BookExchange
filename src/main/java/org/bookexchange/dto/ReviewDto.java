package org.bookexchange.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data

public class ReviewDto {
    private String title;
    private String body;
    private String clientUsername;
}
