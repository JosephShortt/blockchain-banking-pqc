package com.josephshortt.blockchainbank;

import com.josephshortt.blockchainbank.models.Account;
import com.josephshortt.blockchainbank.models.AccountType;
import com.josephshortt.blockchainbank.models.Customer;

public class Main {
    static void main() {
        Customer joe = new Customer("c1","Joe","Shortt","josephshortt1@gmail.com");
        Account joesCurrent = new Account("c1","a1", AccountType.CURRENT,550.45);

        System.out.println(joe.getFirstName()+" "+joe.getSurname()+"\nAccount Type: "+joesCurrent.getAccountType()+"\nAccount Balance: €"+joesCurrent.getBalance());
    }
}
