package com.josephshortt.blockchainbank.blockchain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_keys" , schema = "blockchain")
public class BankKeys {
    @Id
    private String bankId;

    @Column(columnDefinition = "TEXT")
    private String bankPublicKey;

    @Column(columnDefinition = "TEXT")
    private String bankPrivateKey;


    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankPublicKey() {
        return bankPublicKey;
    }

    public void setBankPublicKey(String bankPublicKey) {
        this.bankPublicKey = bankPublicKey;
    }

    public String getBankPrivateKey() {
        return bankPrivateKey;
    }

    public void setBankPrivateKey(String bankPrivateKey) {
        this.bankPrivateKey = bankPrivateKey;
    }
}
