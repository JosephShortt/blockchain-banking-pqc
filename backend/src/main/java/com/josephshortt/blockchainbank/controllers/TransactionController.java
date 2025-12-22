package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.LoadBankAccounts;
import com.josephshortt.blockchainbank.models.Transaction;
import com.josephshortt.blockchainbank.models.TransactionRequest;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import com.josephshortt.blockchainbank.repository.BankAccountRepository;
import com.josephshortt.blockchainbank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/api/accounts/transaction")

public class TransactionController {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping
    public  ResponseEntity<?>  SendMoney(@RequestBody TransactionRequest request){

        DefaultBankAccount senderAccount = request.getAccount();

        double amount = request.getAmount();
        String iban = request.getIban();

        Optional<DefaultBankAccount> optionalReceiver = bankAccountRepository.findByIban(iban);
        if (optionalReceiver.isEmpty()) {
            return ResponseEntity.status(404).body("Account with IBAN " + iban + " not found");
        }
        DefaultBankAccount receiverAccount = optionalReceiver.get();



        // Update balances
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);


        // Save changes to DB
        bankAccountRepository.save(senderAccount);
        bankAccountRepository.save(receiverAccount);


        // Record transaction
        Transaction transaction = new Transaction(senderAccount.getIban(), receiverAccount.getIban(), amount, LocalDateTime.now());
        transactionRepository.save(transaction);


        return ResponseEntity.ok(senderAccount);

    }



}
