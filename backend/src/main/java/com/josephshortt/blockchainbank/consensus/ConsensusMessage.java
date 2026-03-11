package com.josephshortt.blockchainbank.consensus;

public class ConsensusMessage {
    private Long blockNumber;
    private String blockHash;
    private String senderBankId;
    private MessageType messageType;

    public enum MessageType {
        PROPOSE, PREPARE, COMMIT
    }

    // Constructors
    public ConsensusMessage() {}

    public ConsensusMessage(Long blockNumber, String blockHash, String senderBankId, MessageType messageType) {
        this.blockNumber = blockNumber;
        this.blockHash = blockHash;
        this.senderBankId = senderBankId;
        this.messageType = messageType;
    }

    // Getters and setters
    public Long getBlockNumber() { return blockNumber; }
    public void setBlockNumber(Long blockNumber) { this.blockNumber = blockNumber; }

    public String getBlockHash() { return blockHash; }
    public void setBlockHash(String blockHash) { this.blockHash = blockHash; }

    public String getSenderBankId() { return senderBankId; }
    public void setSenderBankId(String senderBankId) { this.senderBankId = senderBankId; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
}

