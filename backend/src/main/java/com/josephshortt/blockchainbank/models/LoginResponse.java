package com.josephshortt.blockchainbank.models;

public class LoginResponse {
    private DefaultBankAccount bankAccount;
    private AccountResponse accountResponse;
    public LoginResponse(AccountResponse accountResponse, DefaultBankAccount bankAccount) {
        this.bankAccount = bankAccount;
        this.accountResponse = accountResponse;
    }

    public AccountResponse getAccountResponse() {
        return accountResponse;
    }

    public void setAccountResponse(AccountResponse accountResponse) {
        this.accountResponse = accountResponse;
    }

    public DefaultBankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(DefaultBankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }


}
