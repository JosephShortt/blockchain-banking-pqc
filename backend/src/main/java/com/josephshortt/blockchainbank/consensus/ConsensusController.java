package com.josephshortt.blockchainbank.consensus;

import com.josephshortt.blockchainbank.blockchain.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consensus")
public class ConsensusController {

    @Autowired
    private ConsensusService consensusService;

    @PostMapping("/propose")
    public ResponseEntity<?> receiveProposal(@RequestBody NetworkService.ProposePayload payload) {
        ConsensusMessage message = payload.getMessage();
        Block block = payload.getBlock();

        consensusService.handleProposal(block, message.getSenderBankId());

        return ResponseEntity.ok("Proposal received");
    }

    @PostMapping("/prepare")
    public ResponseEntity<?> receivePrepare(@RequestBody ConsensusMessage message) {
        consensusService.handlePrepare(
                message.getBlockNumber(),
                message.getBlockHash(),
                message.getSenderBankId()
        );

        return ResponseEntity.ok("Prepare received");
    }

    @PostMapping("/commit")
    public ResponseEntity<?> receiveCommit(@RequestBody ConsensusMessage message) {
        consensusService.handleCommit(
                message.getBlockNumber(),
                message.getBlockHash(),
                message.getSenderBankId()
        );

        return ResponseEntity.ok("Commit received");
    }
}