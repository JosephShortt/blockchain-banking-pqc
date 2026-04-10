package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.blockchain.BlockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BlockTransactionRepository extends JpaRepository<BlockTransaction, Long> {
    List<BlockTransaction> findByBlockIsNull();

    List<BlockTransaction> findByBlockBlockNumber(Long blockNumber);

    Optional<BlockTransaction> findByLocalTransactionId(Long localTransactionId);

}
