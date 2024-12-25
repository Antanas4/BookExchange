package org.bookexchange.dto;

import lombok.*;
import org.bookexchange.model.enums.PublicationStatus;

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
    private PublicationStatus status;
}
