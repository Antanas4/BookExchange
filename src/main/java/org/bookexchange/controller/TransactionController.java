package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.TransactionDto;
import org.bookexchange.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/publications/transactions")
@AllArgsConstructor

public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDto transactionDto) {
        try{
            transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionDto.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
