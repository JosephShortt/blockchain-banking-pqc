package com.josephshortt.blockchainbank;

import com.josephshortt.blockchainbank.controllers.AccountController;
import com.josephshortt.blockchainbank.models.Account;
import com.josephshortt.blockchainbank.models.AccountType;
import com.josephshortt.blockchainbank.models.Customer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);


    }
}
