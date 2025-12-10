package com.josephshortt.blockchainbank.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderIban;
    private String receiverIban;
    private double amount;

    private LocalDateTime timestamp;

    // Constructors, getters, setters
    public Transaction() {}

    public Transaction(String senderIban, String receiverIban, double amount, LocalDateTime timestamp) {
        this.senderIban = senderIban;
        this.receiverIban = receiverIban;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // getters and setters...
}
