package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.TransactionDto;
import org.bookexchange.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor

public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDto transactionDto) {
        try{
            transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionDto.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Integer transactionId) {
        try{
            transactionService.deleteTransaction(transactionId);
            return ResponseEntity.status(HttpStatus.OK).body("Transaction deleted successfully.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getTransactions")
    public ResponseEntity<String> getTransactions() {
        try{
            List<TransactionDto> transactions = transactionService.getTransactions();
            return ResponseEntity.status(HttpStatus.OK).body(transactions.toString());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

//    @PutMapping("/updateTransaction/{transactionId}")
//    public ResponseEntity<String> updateTransaction(@RequestBody TransactionDto transactionDto, @PathVariable Integer transactionId) {
//        try {
//            transactionService.updateTransaction(transactionDto);
//            return ResponseEntity.ok("Publication updated successfully.");
//        } catch (NoSuchElementException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
//        }
//    }
}
