package com.josephshortt.blockchainbank.blockchain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "block_transaction", schema = "blockchain")
public class BlockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txId;

    //Transaction Data
    private LocalDateTime createdAt;
    private String senderIban;
    private String receiverIban;
    private BigDecimal amount;
    private String senderBankId;
    private String receiverBankId;

    @Column(columnDefinition = "TEXT")
    private String senderSignature;

    @Column(columnDefinition = "TEXT")
    private String senderPublicKey;

    //Link transaction to its block
    @ManyToOne
    @JoinColumn(name = "block_number")
    private Block block;

    public BlockTransaction(){
        this.createdAt = LocalDateTime.now();
    }



    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Long getTxId() {
        return txId;
    }

    public void setTxId(Long txId) {
        this.txId = txId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getSenderSignature() {
        return senderSignature;
    }

    public void setSenderSignature(String senderSignature) {
        this.senderSignature = senderSignature;
    }

    public String getReceiverBankId() {
        return receiverBankId;
    }

    public void setReceiverBankId(String receiverBankId) {
        this.receiverBankId = receiverBankId;
    }

    public String getSenderBankId() {
        return senderBankId;
    }

    public void setSenderBankId(String senderBankId) {
        this.senderBankId = senderBankId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
