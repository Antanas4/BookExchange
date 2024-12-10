package org.bookexchange.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String body;
    private LocalDateTime timestamp;
    @ManyToOne
    private Client reviewAuthor;
    @ManyToOne
    private Client reviewRecipient;

    public Review(String title, String body, LocalDateTime currentDateTime, Client author, Client recipient) {
        this.title = title;
        this.body = body;
        this.timestamp = currentDateTime;
        this.reviewAuthor = author;
        this.reviewRecipient = recipient;
    }
}
