package com.josephshortt.blockchainbank.models;

public class AddFundsRequest {
    private DefaultBankAccount account;
    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public DefaultBankAccount getAccount() {
        return account;
    }

    public void setAccount(DefaultBankAccount account) {
        this.account = account;
    }

}
