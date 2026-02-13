package com.josephshortt.blockchainbank.models;

public class TransactionRequest {
    private DefaultBankAccount account;
    private double amount;
    private String iban;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

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
