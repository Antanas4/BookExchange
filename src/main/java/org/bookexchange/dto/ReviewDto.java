package org.bookexchange.dto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReviewDto {
    private String title;
    private String body;
    private LocalDateTime timestamp;
    private String author;
    private String recipient;


}
