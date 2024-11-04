package org.bookexchange.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bookexchange.model.enums.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity


public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int ownerId;
    private int recipientId;

    @ManyToOne
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private LocalDateTime date;

    public Transaction(int ownerId, int recipientId, Publication publication, TransactionType transactionType, LocalDateTime currentDateTime) {
        this.ownerId = ownerId;
        this.recipientId = recipientId;
        this.publication = publication;
        this.type = transactionType;
        this.date = currentDateTime;
    }
}
