package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.blockchain.Block;
import com.josephshortt.blockchainbank.blockchain.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/blockchain")
public class BlockchainController {
    @Autowired
    private BlockchainService blockchainService;

    @PostMapping("/create-block")
    public ResponseEntity<?> createBlock() {
        try{
            blockchainService.createBlock();
            return ResponseEntity.ok("Block creation triggered");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error" + e.getMessage());
        }
    }

    @PostMapping("/validate-block/{blockNumber}")
    public ResponseEntity<?> validateBlock(@PathVariable Long blockNumber) throws Exception {
        Block block = blockchainService.getBlockByNumber(blockNumber).orElseThrow();
        boolean isValid = blockchainService.validateBlock(block);

        return ResponseEntity.ok(Map.of(
           "blockNumber", blockNumber,
                "isValid",isValid,
                "status", block.getStatus()
        ));
    }
}
