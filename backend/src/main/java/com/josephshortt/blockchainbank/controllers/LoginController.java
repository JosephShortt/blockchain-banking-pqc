package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.AddFundsRequest;
import com.josephshortt.blockchainbank.models.CustomerAccount;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import com.josephshortt.blockchainbank.models.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.josephshortt.blockchainbank.controllers.CustomerAccountController.accounts;
import static com.josephshortt.blockchainbank.controllers.CustomerAccountController.defaultBankAccounts;

@RestController
@RequestMapping("/api/accounts/login")
@CrossOrigin(origins = "http://localhost:3000")

public class LoginController {



    @PostMapping
    public ResponseEntity validateLogin(@RequestBody CustomerAccount loginAttempt){

        CustomerAccount matchedCustomer = null;
        for (CustomerAccount acc : accounts) {
            if (acc.getEmail().equals(loginAttempt.getEmail()) &&
                    acc.getPassword().equals(loginAttempt.getPassword())) {
                matchedCustomer = acc;
                break;
            }
        }

        if (matchedCustomer == null) {
            return ResponseEntity.status(401).build(); // no valid user found
        }

        DefaultBankAccount matchedBank = null;
        for (DefaultBankAccount bank : defaultBankAccounts) {
            if (bank.getCustomerId().equals(matchedCustomer.getCustomerId())) {
                matchedBank = bank;
                break;
            }
        }

        LoginResponse response = new LoginResponse(matchedCustomer, matchedBank);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-funds-input")
    public DefaultBankAccount addFunds(@RequestBody AddFundsRequest request){

        DefaultBankAccount defaultBankAccount = request.getAccount();
        double amount = request.getAmount();
        String iban = request.getIban();

        for (DefaultBankAccount bank : defaultBankAccounts) {

            if (bank.getCustomerId().equals(defaultBankAccount.getCustomerId())) {
                bank.setBalance(bank.getBalance() + amount);
                System.out.println("New balance = " + bank.getBalance());
                return bank;
            }
        }
        return null;

    }



}
