package com.josephshortt.blockchainbank.models;

public class Account {
    private String customerId,accountId;
    private AccountType accountType;
    private double balance;

    public Account(String customerId, String accountId, AccountType accountType,double balance){
        this.customerId=customerId;
        this.accountId=accountId;
        this.accountType=accountType;
        this.balance=balance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

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
