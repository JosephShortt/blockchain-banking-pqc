package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.LoadBankAccounts;
import com.josephshortt.blockchainbank.models.TransactionRequest;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import com.josephshortt.blockchainbank.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/accounts/transaction")

public class TransactionController {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @PostMapping
    public  ResponseEntity<?>  SendMoney(@RequestBody TransactionRequest request){

        DefaultBankAccount defaultBankAccount = request.getAccount();
        double amount = request.getAmount();
        String iban = request.getIban();

        Optional<DefaultBankAccount> optionalAccount = bankAccountRepository.findByIban(iban);
        if (optionalAccount.isEmpty()) {
            return ResponseEntity.status(404).body("Account with IBAN " + iban + " not found");
        }

        DefaultBankAccount bankAccount = optionalAccount.get();
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);

        return ResponseEntity.ok(bankAccount);

    }
}
