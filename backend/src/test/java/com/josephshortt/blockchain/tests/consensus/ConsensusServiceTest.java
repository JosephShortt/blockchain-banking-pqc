package com.josephshortt.blockchain.tests.consensus;

import com.josephshortt.blockchainbank.blockchain.*;
import com.josephshortt.blockchainbank.consensus.ConsensusService;
import com.josephshortt.blockchainbank.consensus.NetworkService;
import com.josephshortt.blockchainbank.repository.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConsensusServiceTest {

    @Mock
    private BlockchainService blockchainService;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private NetworkService networkService;

    @InjectMocks
    private ConsensusService consensusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(consensusService, "bankId", "bank-a");
    }

    // ========== PREPARE VOTE TESTS ==========

    @Test
    public void testSinglePrepareVoteDoesNotTriggerCommit() {
        // Only 1 vote, need 2 — should not send commit
        consensusService.handlePrepare(1L, "hash123", "bank-b");

        verify(networkService, never()).broadcastMessage(any(), any());
    }

    @Test
    public void testTwoPrepareVotesTriggersCommit() {
        Block block = new Block();
        block.setBlockNumber(1L);
        block.setStatus(BlockStatus.PROPOSED);

        when(blockchainService.getBlockByNumber(1L)).thenReturn(Optional.of(block));

        consensusService.handlePrepare(1L, "hash123", "bank-b");
        consensusService.handlePrepare(1L, "hash123", "bank-c");

        // Should have broadcasted commit
        verify(networkService, atLeastOnce()).broadcastMessage(any(), any());
    }


    // ========== COMMIT VOTE TESTS ==========

    @Test
    public void testSingleCommitVoteDoesNotFinalize() {
        Block block = new Block();
        block.setBlockNumber(1L);
        block.setStatus(BlockStatus.PROPOSED);

        when(blockchainService.getBlockByNumber(1L)).thenReturn(Optional.of(block));

        consensusService.handleCommit(1L, "hash123", "bank-b");

        // Block should not be saved as finalized with only 1 commit
        verify(blockRepository, never()).save(any());
    }

    @Test
    public void testTwoCommitVotesFinalizeBlock() {
        Block block = new Block();
        block.setBlockNumber(1L);
        block.setStatus(BlockStatus.PROPOSED);
        block.setTransactions(new ArrayList<>());

        when(blockchainService.getBlockByNumber(1L)).thenReturn(Optional.of(block));
        doNothing().when(blockchainService).markLocalTransactionsAsProcessed(any());
        doNothing().when(blockchainService).processIncomingTransactions(any(), any());
        doNothing().when(blockchainService).settleBankReserves(any(), any());

        consensusService.handleCommit(1L, "hash123", "bank-b");
        consensusService.handleCommit(1L, "hash123", "bank-c");

        verify(blockRepository, times(1)).save(any());
    }


    // ========== PROPOSAL TESTS ==========

    @Test
    public void testInvalidBlockIsRejected() throws Exception {
        Block block = new Block();
        block.setBlockNumber(1L);
        block.setTransactions(new ArrayList<>());

        when(blockchainService.validateBlock(block)).thenReturn(false);

        consensusService.handleProposal(block, "bank-b");

        // Should not save invalid block
        verify(blockRepository, never()).save(any());
        // Should not broadcast prepare
        verify(networkService, never()).broadcastMessage(any(), any());
    }

    @Test
    public void testValidBlockIsAcceptedAndPrepareIsSent() throws Exception {
        Block block = new Block();
        block.setBlockNumber(1L);
        block.setTransactions(new ArrayList<>());

        when(blockchainService.validateBlock(block)).thenReturn(true);
        when(blockRepository.findByBlockNumber(1L)).thenReturn(Optional.empty());

        consensusService.handleProposal(block, "bank-b");

        // Should broadcast prepare
        verify(networkService, atLeastOnce()).broadcastMessage(any(), any());
    }

}