package org.bookexchange.dto;

import lombok.*;
import org.bookexchange.model.enums.TransactionType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TransactionDto {
    private int ownerId;
    private int recipientId;
    private int publicationId;
    private TransactionType transactionType;
}
