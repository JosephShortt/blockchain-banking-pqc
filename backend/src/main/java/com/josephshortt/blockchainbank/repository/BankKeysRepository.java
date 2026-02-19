package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.blockchain.BankKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankKeysRepository extends JpaRepository<BankKeys, String> {
    Optional<BankKeys> findByBankId(String bankId);
}
