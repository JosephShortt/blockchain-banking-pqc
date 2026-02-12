package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.blockchain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block,Long> {
}
