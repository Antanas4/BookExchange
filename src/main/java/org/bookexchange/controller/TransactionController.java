package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.TransactionDto;
import org.bookexchange.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor

public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDto transactionDto) {
        transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDto.toString());
    }

    @GetMapping("/getTransactions")
    public ResponseEntity<List<TransactionDto>> getTransactions() {
        List<TransactionDto> transactions = transactionService.getTransactions();
        return ResponseEntity.ok(transactions);
    }
}
