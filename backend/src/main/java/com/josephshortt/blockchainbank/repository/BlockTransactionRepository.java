package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.blockchain.BlockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BlockTransactionRepository extends JpaRepository<BlockTransaction, Long> {
    List<BlockTransaction> findByBlockIsNull();
    List<BlockTransaction> findByBlockBlockNumber(Long blockNumber);
    Optional<BlockTransaction> findBySenderIbanAndReceiverIbanAndAmount(String senderIban, String receiverIban, BigDecimal amount);

}
