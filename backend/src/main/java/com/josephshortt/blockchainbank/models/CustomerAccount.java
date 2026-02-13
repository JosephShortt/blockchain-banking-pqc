package com.josephshortt.blockchainbank.models;

import jakarta.persistence.*;

import java.security.PrivateKey;
import java.security.PublicKey;

@Entity
public class CustomerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    private String firstName,surname,email,password;

    private String bankId;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey;

    @Column(columnDefinition = "TEXT")
    private String publicKey;

    public CustomerAccount() {
    }

    public CustomerAccount(Long customerId, String firstName, String surname, String email,String password, String bankId) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password=password;
        this.bankId = bankId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
