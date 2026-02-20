package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.blockchain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block,Long> {
    Optional<Block> findFirstByOrderByBlockNumberDesc();
    Optional<Block> findByBlockNumber(Long blockNumber);
}
