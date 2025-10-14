package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.AccountType;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import com.josephshortt.blockchainbank.models.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.josephshortt.blockchainbank.models.CustomerAccount;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerAccountController {

    public static final List<CustomerAccount> accounts = new ArrayList<>();
    public static final List<DefaultBankAccount> defaultBankAccounts = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody CustomerAccount customerAccount) {

        for(CustomerAccount account : accounts){
            if(customerAccount.getEmail().equals(account.getEmail())){
                return ResponseEntity.status(401).build();
            }
        }


        String generatedCustomerId = "c"+ (accounts.size() + 1);
        customerAccount.setCustomerId(generatedCustomerId);

        DefaultBankAccount defaultBankAccount = new DefaultBankAccount(
                customerAccount.getCustomerId(),
                "A"+customerAccount.getCustomerId(),
                AccountType.CURRENT,
                0);

        accounts.add(customerAccount);
        defaultBankAccounts.add(defaultBankAccount);
        System.out.println("Created customer: " + customerAccount.getFirstName());


        return ResponseEntity.ok(customerAccount);


    }

    @GetMapping
    public List<CustomerAccount> getAllAccounts() {
        return accounts;
    }


}
