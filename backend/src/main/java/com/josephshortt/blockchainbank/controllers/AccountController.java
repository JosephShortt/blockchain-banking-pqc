package com.josephshortt.blockchainbank.controllers;

import org.springframework.web.bind.annotation.*;
import com.josephshortt.blockchainbank.models.Customer;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:3000") // allow your React app to call this API
public class AccountController {

    @PostMapping
    public Customer createAccount(@RequestBody Customer customer) {
        System.out.println("Received new customer: " + customer.getFirstName());
        // In future: save to DB or blockchain ledger
        return customer; // send it back to frontend (optional)
    }
}
