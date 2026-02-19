package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.blockchain.BlockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockTransactionRepository extends JpaRepository<BlockTransaction, Long> {
    List<BlockTransaction> findByBlockIsNull();

}
