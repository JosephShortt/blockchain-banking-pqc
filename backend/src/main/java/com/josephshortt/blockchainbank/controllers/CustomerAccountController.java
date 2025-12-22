package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.*;
import com.josephshortt.blockchainbank.repository.BankAccountRepository;
import com.josephshortt.blockchainbank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class CustomerAccountController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody CustomerAccount customerAccount) {

        if(customerAccount.getFirstName().isEmpty() || customerAccount.getSurname().isEmpty()
                || customerAccount.getEmail().isEmpty() || customerAccount.getPassword().isEmpty()){
            return ResponseEntity.status(401).body("Please fill in all fields!!");
        }


        for(CustomerAccount account : LoadCustomerAccounts.accounts){
            if(customerAccount.getEmail().equals(account.getEmail())){
                return ResponseEntity.status(401) .body("Email is already in use");
            }
        }

        //Hash the passed password
        HashPassword hash = new HashPassword(customerAccount.getPassword());
        String hashedPassword = hash.digestAndEncode(customerAccount.getPassword());
        customerAccount.setPassword(hashedPassword);

        customerRepository.save(customerAccount);
        LoadCustomerAccounts.accounts.add(customerAccount);
        //Generate Iban with id
        String generatedIban = "IBAN"+customerAccount.getCustomerId();

        //Create default bank account for created account
        DefaultBankAccount defaultBankAccount = new DefaultBankAccount(
                customerAccount.getCustomerId(),
                "A"+customerAccount.getCustomerId(),
                generatedIban,
                AccountType.CURRENT,
                0);

        bankAccountRepository.save(defaultBankAccount);
        LoadBankAccounts.defaultBankAccounts.add(defaultBankAccount);

        System.out.println("Created customer: " + customerAccount.getFirstName());


        return ResponseEntity.ok(customerAccount);


    }

    @GetMapping
    public List<CustomerAccount> getAllAccounts() {
        return LoadCustomerAccounts.accounts;
    }


}
