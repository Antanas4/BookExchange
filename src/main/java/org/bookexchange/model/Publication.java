package org.bookexchange.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bookexchange.dto.PublicationDto;
import org.bookexchange.model.enums.PublicationStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String author;
    private double price;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client owner;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Client borrower;

    @Enumerated(EnumType.STRING)
    private PublicationStatus status;

    public abstract PublicationDto toDto();
}
