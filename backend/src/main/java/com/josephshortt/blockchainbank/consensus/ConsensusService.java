package com.josephshortt.blockchainbank.consensus;

import com.josephshortt.blockchainbank.blockchain.*;
import com.josephshortt.blockchainbank.repository.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsensusService {

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private NetworkService networkService;

    @Value("${bank.id}")
    private String bankId;

    // Track votes per block
    // Key: blockNumber, Value: Set of bankIds that voted
    private Map<Long, Set<String>> prepareVotes = new ConcurrentHashMap<>();
    private Map<Long, Set<String>> commitVotes = new ConcurrentHashMap<>();
    private Map<Long, List<BlockTransaction>> pendingBlockTransactions = new ConcurrentHashMap<>();
    private final int TOTAL_BANKS = 3;  // bank-a, bank-b, bank-c
    private final int REQUIRED_VOTES = 2;  // 2f+1 where f=1 (can tolerate 1 fault)


    // ======== STEP 1: PROPOSE =========

    public void initiateConsensus(Block block) {
        System.out.println("=== INITIATING CONSENSUS FOR BLOCK " + block.getBlockNumber() + " ===");

        // Initialize vote tracking
        prepareVotes.put(block.getBlockNumber(), new HashSet<>());
        commitVotes.put(block.getBlockNumber(), new HashSet<>());

        // Store transactions BEFORE broadcasting
        if (block.getTransactions() != null) {
            pendingBlockTransactions.put(block.getBlockNumber(), block.getTransactions());
        }
        
        // Broadcast PROPOSE to all other banks
        ConsensusMessage proposeMsg = new ConsensusMessage(
                block.getBlockNumber(),
                block.getHash(),
                bankId,
                ConsensusMessage.MessageType.PROPOSE
        );

        networkService.broadcastMessage(proposeMsg, block);

        // Also validate and prepare our own block
        handleProposal(block, bankId);
    }


    // ========== STEP 2: PREPARE =========

    public void handleProposal(Block block, String fromBankId) {
        System.out.println("Received PROPOSE for Block " + block.getBlockNumber() + " from " + fromBankId);
        System.out.println("Transactions received: " + (block.getTransactions() == null ? "NULL" : block.getTransactions().size()));


        try {
            // Validate the proposed block
            if (blockchainService.validateBlock(block)) {
                System.out.println("Block " + block.getBlockNumber() + " is valid");

                // Store transactions for finalization
                if (block.getTransactions() != null) {
                    pendingBlockTransactions.put(block.getBlockNumber(), block.getTransactions());
                }

                // Save block if we don't have it yet
                if (blockRepository.findByBlockNumber(block.getBlockNumber()).isEmpty()) {
                    if (block.getTransactions() != null) {
                        for (BlockTransaction tx : block.getTransactions()) {
                            tx.setTxId(null); // let DB assign new ID
                            tx.setBlock(block);
                        }
                    }
                    blockRepository.save(block);
                }


                // Send PREPARE vote to all banks
                ConsensusMessage prepareMsg = new ConsensusMessage(
                        block.getBlockNumber(),
                        block.getHash(),
                        bankId,
                        ConsensusMessage.MessageType.PREPARE
                );

                networkService.broadcastMessage(prepareMsg, null);

                // Also count our own vote
                handlePrepare(block.getBlockNumber(), block.getHash(), bankId);

            } else {
                System.out.println("Block " + block.getBlockNumber() + " is INVALID - rejecting");
            }
        } catch (Exception e) {
            System.err.println("Error validating block: " + e.getMessage());
        }
    }

    public void handlePrepare(Long blockNumber, String blockHash, String fromBankId) {
        System.out.println("Received PREPARE for Block " + blockNumber + " from " + fromBankId);

        // Get or create vote set
        Set<String> votes = prepareVotes.computeIfAbsent(blockNumber, k -> new HashSet<>());

        // Add vote
        votes.add(fromBankId);

        System.out.println("PREPARE votes for Block " + blockNumber + ": " + votes.size() + "/" + REQUIRED_VOTES);

        // If we have enough PREPARE votes → send COMMIT
        if (votes.size() >= REQUIRED_VOTES && !hasCommitted(blockNumber)) {
            System.out.println("Sufficient PREPARE votes for Block " + blockNumber + " - sending COMMIT");

            ConsensusMessage commitMsg = new ConsensusMessage(
                    blockNumber,
                    blockHash,
                    bankId,
                    ConsensusMessage.MessageType.COMMIT
            );

            networkService.broadcastMessage(commitMsg, null);

            // Also count our own commit
            handleCommit(blockNumber, blockHash, bankId);
        }
    }


    // ========== STEP 3: COMMIT ==========

    public void handleCommit(Long blockNumber, String blockHash, String fromBankId) {
        System.out.println("Received COMMIT for Block " + blockNumber + " from " + fromBankId);

        // Get or create commit set
        Set<String> commits = commitVotes.computeIfAbsent(blockNumber, k -> new HashSet<>());

        // Add commit
        commits.add(fromBankId);

        System.out.println("COMMIT votes for Block " + blockNumber + ": " + commits.size() + "/" + REQUIRED_VOTES);

        // If we have enough COMMIT votes → FINALIZE
        if (commits.size() >= REQUIRED_VOTES) {
            finalizeBlock(blockNumber);
        }
    }


    // ========== STEP 4: FINALIZE =========

    private void finalizeBlock(Long blockNumber) {
        Optional<Block> blockOpt = blockchainService.getBlockByNumber(blockNumber);

        if (blockOpt.isEmpty()) {
            System.out.println("Block " + blockNumber + " not found locally - skipping finalization");
            prepareVotes.remove(blockNumber);
            commitVotes.remove(blockNumber);
            pendingBlockTransactions.remove(blockNumber);
            return;
        }

        Block block = blockOpt.get();
        List<BlockTransaction> txs = pendingBlockTransactions.getOrDefault(blockNumber, new ArrayList<>());
        System.out.println("TXS in memory for block " + blockNumber + ": " + txs.size());


        // Check if already finalized
        if (block.getStatus() == BlockStatus.FINALIZED) {
            System.out.println("Block " + blockNumber + " already finalized by another bank");

            blockchainService.markLocalTransactionsAsProcessed(block);
            blockchainService.processIncomingTransactions(block, txs);
            blockchainService.settleBankReserves(block, txs);

            // Clean up vote tracking
            prepareVotes.remove(blockNumber);
            commitVotes.remove(blockNumber);
            pendingBlockTransactions.remove(blockNumber);

            System.out.println("Processed settlements for Block " + blockNumber);
            return;
        }

        System.out.println("CONSENSUS REACHED - FINALIZING BLOCK " + blockNumber);

        // Update status
        block.setStatus(BlockStatus.FINALIZED);
        blockRepository.save(block);


        blockchainService.markLocalTransactionsAsProcessed(block);
        // Process settlements
        blockchainService.processIncomingTransactions(block, txs);
        blockchainService.settleBankReserves(block, txs);


        // Clean up vote tracking
        prepareVotes.remove(blockNumber);
        commitVotes.remove(blockNumber);
        pendingBlockTransactions.remove(blockNumber);

        System.out.println("Block " + blockNumber + " finalized by consensus!");
    }


    // helper methods

    private boolean hasCommitted(Long blockNumber) {
        Set<String> commits = commitVotes.get(blockNumber);
        return commits != null && commits.contains(bankId);
    }
}
