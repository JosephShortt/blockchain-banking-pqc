package com.josephshortt.blockchainbank.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderIban;
    private String receiverIban;
    private BigDecimal amount;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    // Constructors, getters, setters
    public Transaction() {}

    public Transaction(String senderIban, String receiverIban, BigDecimal amount, LocalDateTime timestamp) {
        this.senderIban = senderIban;
        this.receiverIban = receiverIban;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getSenderIban() {
        return senderIban;
    }

    public void setSenderIban(String senderIban) {
        this.senderIban = senderIban;
    }

    public String getReceiverIban() {
        return receiverIban;
    }

    public void setReceiverIban(String receiverIban) {
        this.receiverIban = receiverIban;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
