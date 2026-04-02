package com.josephshortt.blockchainbank.blockchain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BlockScheduler {
    @Autowired
    private BlockchainService blockchainService;

    @Scheduled(fixedDelay = 30000) // every 30 seconds
    public void scheduleBlockCreation() {
        try {
            blockchainService.createBlock();
        } catch (Exception e) {
            System.err.println("Scheduled block creation failed: " + e.getMessage());
        }
    }
}
