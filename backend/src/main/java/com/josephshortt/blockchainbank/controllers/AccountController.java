package com.josephshortt.blockchainbank.controllers;

import org.springframework.web.bind.annotation.*;
import com.josephshortt.blockchainbank.models.Customer;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    @PostMapping
    public Customer createAccount(@RequestBody Customer customer) {
        System.out.println("Received new customer: " + customer.getFirstName());
        return customer;
    }
}
