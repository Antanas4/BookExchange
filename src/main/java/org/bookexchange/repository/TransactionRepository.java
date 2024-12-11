package org.bookexchange.repository;

import org.bookexchange.model.Transaction;
import org.bookexchange.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByRecipientIdAndType(int recipientId, TransactionType type);
}
