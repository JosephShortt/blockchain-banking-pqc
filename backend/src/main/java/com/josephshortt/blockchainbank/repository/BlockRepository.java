package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.blockchain.Block;
import com.josephshortt.blockchainbank.blockchain.BlockStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block,Long> {
    Optional<Block> findFirstByOrderByBlockNumberDesc();
    Optional<Block> findByBlockNumber(Long blockNumber);
    List<Block> findByStatusOrderByBlockNumberAsc(BlockStatus status);
}
