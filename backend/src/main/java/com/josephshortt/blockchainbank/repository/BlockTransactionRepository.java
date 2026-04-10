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

    @Query("SELECT bt FROM BlockTransaction bt WHERE bt.senderIban = :senderIban AND bt.receiverIban = :receiverIban AND bt.amount = :amount AND bt.createdAt BETWEEN :start AND :end")
    List<BlockTransaction> findByDetails(
            @Param("senderIban") String senderIban,
            @Param("receiverIban") String receiverIban,
            @Param("amount") BigDecimal amount,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
