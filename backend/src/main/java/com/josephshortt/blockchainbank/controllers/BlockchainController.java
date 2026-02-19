package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.blockchain.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
