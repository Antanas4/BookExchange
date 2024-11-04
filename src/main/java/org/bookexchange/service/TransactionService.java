package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.TransactionDto;
import org.bookexchange.model.Publication;
import org.bookexchange.model.Transaction;
import org.bookexchange.repository.PublicationRepository;
import org.bookexchange.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PublicationRepository publicationRepository;

    public void createTransaction(TransactionDto transactionDto) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Optional<Publication> optionalPublication = publicationRepository.findById(transactionDto.getPublicationId());

        if (optionalPublication.isPresent()) {
            Publication publication = optionalPublication.get();
            Transaction transaction = new Transaction(
                    transactionDto.getOwnerId(),
                    transactionDto.getRecipientId(),
                    publication,
                    transactionDto.getTransactionType(),
                    currentDateTime
            );
            transactionRepository.save(transaction);
        } else{
            throw new NoSuchElementException("Publication not found");
        }
    }
}
