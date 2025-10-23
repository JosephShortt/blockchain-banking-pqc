package com.josephshortt.blockchainbank.models;

public class AccountResponse {
    private long customerId;
    private String firstName,surname,email;

    public AccountResponse(long customerId, String firstName, String surname, String email) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
