package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.PublicationDto;
import org.bookexchange.dto.TransactionDto;
import org.bookexchange.model.Publication;
import org.bookexchange.model.Transaction;
import org.bookexchange.repository.PublicationRepository;
import org.bookexchange.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public void deleteTransaction(Integer transactionId) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            transactionRepository.delete(transaction);
        } else {
            throw new NoSuchElementException("Transaction not found");
        }
    }

    public List<TransactionDto> getTransactions() {
        List<TransactionDto> transactionDtos= new ArrayList<>();
        List<Transaction> transactions = transactionRepository.findAll();
        for (Transaction transaction : transactions) {
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setOwnerId(transaction.getOwnerId());
            transactionDto.setRecipientId(transaction.getRecipientId());
            transactionDto.setPublicationId(transaction.getId());
            transactionDto.setTransactionType(transaction.getType());
            transactionDtos.add(transactionDto);
        }
        return transactionDtos;
    }

//    public void updateTransaction(TransactionDto transactionDto) {
//        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionDto.getPublicationId());
//        if (optionalTransaction.isPresent()) {
//            Transaction transaction = optionalTransaction.get();
//            transaction.setType(transactionDto.getTransactionType());
//            transaction.setOwnerId(transactionDto.getOwnerId());
//            transaction.setRecipientId(transactionDto.getRecipientId());
//            transactionRepository.save(transaction);
//        } else {
//            throw new NoSuchElementException("Transaction not found");
//        }
//    }
}
