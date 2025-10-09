package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.models.AccountType;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import org.springframework.web.bind.annotation.*;
import com.josephshortt.blockchainbank.models.CustomerAccount;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerAccountController {

    @PostMapping
    public CustomerAccount createAccount(@RequestBody CustomerAccount customerAccount) {
        System.out.println("Received new customer: " + customerAccount.getFirstName());
        System.out.println("Creating default bank account for "+customerAccount.getFirstName());
        DefaultBankAccount defaultBankAccount = new DefaultBankAccount(customerAccount.getCustomerId(),"a"+customerAccount.getCustomerId(), AccountType.CURRENT,0);
        System.out.println("****Customer Account Created****\n" +
                "Name: "+customerAccount.getFirstName()+" "+customerAccount.getSurname()+"\n" +
                "Customer ID: "+customerAccount.getCustomerId()+"\n" +
                "Email: "+customerAccount.getEmail()+"\n" +
                "****Customer Current Bank Account Created****"+"\n" +
                "Customer Id: "+defaultBankAccount.getCustomerId()+"\n" +
                "Account ID: "+defaultBankAccount.getAccountId()+"\n" +
                "Account Type: "+defaultBankAccount.getAccountType()+"\n" +
                "Balance: "+defaultBankAccount.getBalance());
        return customerAccount;
    }
}
