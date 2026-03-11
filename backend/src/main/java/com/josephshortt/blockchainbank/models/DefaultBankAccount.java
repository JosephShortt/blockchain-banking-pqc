package com.josephshortt.blockchainbank.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.math.BigDecimal;

import static com.josephshortt.blockchainbank.models.AccountType.CURRENT;
@Entity
public class DefaultBankAccount {
    @Id
    private Long customerId;
    private String accountId,iban;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal balance;
    private String bankId;

    public DefaultBankAccount(){}

    public DefaultBankAccount(Long customerId, String accountId, String iban, AccountType accountType, BigDecimal balance, String bankId){
        this.customerId=customerId;
        this.accountId=accountId;
        this.iban = iban;
        this.accountType=accountType;
        this.balance=balance;
        this.bankId = bankId;
    }


    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
