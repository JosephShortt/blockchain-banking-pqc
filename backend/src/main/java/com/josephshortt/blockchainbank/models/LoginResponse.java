package com.josephshortt.blockchainbank.models;

public class LoginResponse {
    private CustomerAccount customer;
    private DefaultBankAccount bankAccount;

    public LoginResponse(CustomerAccount customer, DefaultBankAccount bankAccount) {
        this.customer = customer;
        this.bankAccount = bankAccount;
    }

    public CustomerAccount getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerAccount customer) {
        this.customer = customer;
    }

    public DefaultBankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(DefaultBankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
}
