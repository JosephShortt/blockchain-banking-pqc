package com.josephshortt.blockchainbank.models;

import com.josephshortt.blockchainbank.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LoadCustomerAccounts {
    public static final List<CustomerAccount> accounts = new ArrayList<>();

    @Autowired
    private CustomerRepository customerRepository;

    @PostConstruct
    public void loadAccounts(){

        List<CustomerAccount> allCustomers = customerRepository.findAll();
        accounts.clear();
        accounts.addAll(allCustomers);

        System.out.println("Loaded " + accounts.size() + " customer accounts from database.");
        for(CustomerAccount account : accounts){
            System.out.println("ID: " + account.getCustomerId() + " Name: " + account.getFirstName());

        }
    }

    public List<CustomerAccount> getAccounts() {
        return accounts;
    }
}
