package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import static com.josephshortt.blockchainbank.controllers.CustomerAccountController.accounts;
import static com.josephshortt.blockchainbank.controllers.CustomerAccountController.defaultBankAccounts;

@RestController
@RequestMapping("/api/accounts/login")
@CrossOrigin(origins = "http://localhost:3000")

public class LoginController {



    @PostMapping
    public ResponseEntity validateLogin(@RequestBody CustomerAccount loginAttempt){

        HashPassword hash = new HashPassword(loginAttempt.getPassword());
        String hashedPassword = hash.digestAndEncode(loginAttempt.getPassword());
        loginAttempt.setPassword(hashedPassword);


        AccountResponse accountResponse = new AccountResponse(loginAttempt.getCustomerId(),loginAttempt.getFirstName(), loginAttempt.getSurname(), loginAttempt.getEmail());

        for (CustomerAccount acc : accounts) {
            if (acc.getEmail().equals(loginAttempt.getEmail()) &&
                    acc.getPassword().equals(loginAttempt.getPassword())) {
                accountResponse.setCustomerId(acc.getCustomerId());
                accountResponse.setFirstName(acc.getFirstName());
                accountResponse.setSurname(acc.getFirstName());
                accountResponse.setEmail(acc.getEmail());
                break;
            }
        }

        if (accountResponse.getCustomerId() == null) {
            return ResponseEntity.status(401).build(); // no valid user found
        }

        DefaultBankAccount matchedBank = null;
        for (DefaultBankAccount bank : defaultBankAccounts) {
            if (bank.getCustomerId().equals(accountResponse.getCustomerId())) {
                matchedBank = bank;
                break;
            }
        }

        LoginResponse response = new LoginResponse(accountResponse, matchedBank);
        return ResponseEntity.ok(response);
    }


}
