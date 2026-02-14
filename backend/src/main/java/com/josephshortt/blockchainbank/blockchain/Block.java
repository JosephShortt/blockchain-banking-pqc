package com.josephshortt.blockchainbank.blockchain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "block", schema = "blockchain")
public class Block {
    @Id
    private Long blockNumber;

    private String prevHash;
    private String hash;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "block")
    private List<BlockTransaction> transactions;

    private String merkleRoot;
    private String proposerId;

    @Enumerated(EnumType.STRING)
    private BlockStatus status;

    @Column(columnDefinition = "TEXT")
    private String blockSignature;

    public Block(){
        this.createdAt = LocalDateTime.now();
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BlockTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<BlockTransaction> transactions) {
        this.transactions = transactions;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public String getProposerId() {
        return proposerId;
    }

    public void setProposerId(String proposerId) {
        this.proposerId = proposerId;
    }

    public BlockStatus getStatus() {
        return status;
    }

    public void setStatus(BlockStatus status) {
        this.status = status;
    }
}
