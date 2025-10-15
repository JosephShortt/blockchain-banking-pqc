package com.josephshortt.blockchainbank.models;

public class AccountResponse {
    private String customerId,firstName,surname,email;

    public AccountResponse(String customerId, String firstName, String surname, String email) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
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
