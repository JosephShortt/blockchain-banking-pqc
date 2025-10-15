package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.TransactionRequest;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import org.springframework.web.bind.annotation.*;

import static com.josephshortt.blockchainbank.controllers.CustomerAccountController.defaultBankAccounts;

@RestController
@RequestMapping("/api/accounts/transaction")
@CrossOrigin(origins = "http://localhost:3000")

public class TransactionController {

    @PostMapping
    public DefaultBankAccount SendMoney(@RequestBody TransactionRequest request){

        DefaultBankAccount defaultBankAccount = request.getAccount();
        double amount = request.getAmount();
        String iban = request.getIban();

        for (DefaultBankAccount bank : defaultBankAccounts) {

            if (bank.getIban().equals(request.getIban())) {
                bank.setBalance(bank.getBalance() + amount);
                System.out.println("New balance = " + bank.getBalance());
                return bank;
            }
        }
        return null;

    }
}
