package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.AccountType;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
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
    public CustomerAccount createAccount(@RequestBody CustomerAccount customerAccount) {
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

        return customerAccount;
    }

    @GetMapping
    public List<CustomerAccount> getAllAccounts() {
        return accounts;
    }
}
