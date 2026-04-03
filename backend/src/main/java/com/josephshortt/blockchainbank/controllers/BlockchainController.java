package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.blockchain.Block;
import com.josephshortt.blockchainbank.blockchain.BlockStatus;
import com.josephshortt.blockchainbank.blockchain.BlockTransaction;
import com.josephshortt.blockchainbank.blockchain.BlockchainService;
import com.josephshortt.blockchainbank.repository.BlockRepository;
import com.josephshortt.blockchainbank.repository.BlockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/blockchain")
public class BlockchainController {
    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockTransactionRepository blockTransactionRepository;

    @Autowired
    private BlockTransaction blockTransaction;

    @PostMapping("/create-block")
    public ResponseEntity<?> createBlock() {
        try{
            blockchainService.createBlock();
            return ResponseEntity.ok("Block creation triggered");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error" + e.getMessage());
        }
    }

    @GetMapping("/validate-block/{blockNumber}")
    public ResponseEntity<?> validateBlock(@PathVariable Long blockNumber) throws Exception {
        Block block = blockchainService.getBlockByNumber(blockNumber).orElseThrow();
        boolean isValid = blockchainService.validateBlock(block);

        return ResponseEntity.ok(Map.of(
           "blockNumber", blockNumber,
                "isValid",isValid,
                "status", block.getStatus()
        ));
    }

    /*
    @PostMapping("/finalize-block/{blockNumber}")
    public ResponseEntity<?> finalizeBlock(@PathVariable Long blockNumber) {
        try {
            Block block = blockchainService.getBlockByNumber(blockNumber).orElseThrow();

            // Validate block
            if (!blockchainService.validateBlock(block)) {
                return ResponseEntity.status(400).body("Block validation failed");
            }

            // Update status to finalized
            block.setStatus(BlockStatus.FINALIZED);
            blockRepository.save(block);

            // Process incoming transactions (credit customer accounts)
            blockchainService.processIncomingTransactions(block);

            // Settle bank reserves (wholesale settlement)
            blockchainService.settleBankReserves(block);  // ← ADD THIS

            return ResponseEntity.ok(Map.of(
                    "message", "Block " + blockNumber + " finalized",
                    "transactions", block.getTransactions().size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
     */


    @GetMapping("/block/{blockNumber}")
    public ResponseEntity<?> getBlock(@PathVariable Long blockNumber) {
        try {
            Block block = blockchainService.getBlockByNumber(blockNumber).orElseThrow();
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Block not found");
        }
    }

    @GetMapping("/chain")
    public ResponseEntity<?> getChain() {
        return ResponseEntity.ok(blockRepository.findAll());
    }

    @GetMapping("/pending-transactions")
    public ResponseEntity<?> getPendingTransactions() {
        return ResponseEntity.ok(blockchainService.getPendingTransactions());
    }

    @GetMapping("/block/{blockNumber}/transactions")
    public ResponseEntity<?> getBlockTransactions(@PathVariable Long blockNumber) {
        return ResponseEntity.ok(blockTransactionRepository.findByBlockBlockNumber(blockNumber));
    }

    @GetMapping("/block/{blockNumber}/settlements")
    public ResponseEntity<?> getBlockSettlements(@PathVariable Long blockNumber) {
        List<BlockTransaction> txs = blockTransactionRepository.findByBlockBlockNumber(blockNumber);

        Block block = blockRepository.findByBlockNumber(blockNumber).orElseThrow();
        Map<String, BigDecimal> netPositions = blockchainService.calculateNetPositions(block);

        Map<String, Object> result = new HashMap<>();
        result.put("transactions", txs);
        result.put("netSettlements", netPositions);

        return ResponseEntity.ok(result);
    }
}
