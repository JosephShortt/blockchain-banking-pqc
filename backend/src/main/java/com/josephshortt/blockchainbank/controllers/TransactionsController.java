package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.Transaction;
import com.josephshortt.blockchainbank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/transactions")

public class TransactionsController {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Get all transactions related to a specific IBAN (sent or received)
     */
    @GetMapping("/{iban}")
    public ResponseEntity<List<Transaction>> getTransactionsForAccount(@PathVariable String iban) {
        // find all transactions where the account is either sender or receiver
        List<Transaction> transactions = transactionRepository.findBySenderIbanOrReceiverIban(iban, iban);

        return ResponseEntity.ok(transactions);
    }
}
