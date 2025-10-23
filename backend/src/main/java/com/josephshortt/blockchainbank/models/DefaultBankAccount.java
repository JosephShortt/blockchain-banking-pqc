package com.josephshortt.blockchainbank.models;

import static com.josephshortt.blockchainbank.models.AccountType.CURRENT;

public class DefaultBankAccount {
    private long customerId;
    private String accountId,iban;
    private AccountType accountType;
    private double balance;



    public DefaultBankAccount(long customerId, String accountId, String iban, AccountType accountType, double balance){
        this.customerId=customerId;
        this.accountId=accountId;
        this.iban = iban;
        this.accountType=accountType;
        this.balance=balance;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getIban() {return iban;}

    public void setIban(String iban) {this.iban = iban;}

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
