package org.bookexchange.dto;

import lombok.*;
import org.bookexchange.model.enums.PublicationStatus;

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
    private PublicationStatus status;

    public PublicationDto(int id, String author, String title, double price, String publicationType, String username, PublicationStatus publicationStatus) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.price = price;
        this.publicationType = publicationType;
        this.ownerUsername = username;
        this.status = publicationStatus;
    }
}
