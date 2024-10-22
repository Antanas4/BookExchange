package org.bookexchange.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PublicationDto {
    private int id;
    private String title;
    private String author;
    private double price;
    private String genre;
    private int issueNumber;
    private String illustrator;
    private String publicationType;
    private String ownerUsername;
}
