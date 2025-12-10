package com.josephshortt.blockchainbank.repository;


import com.josephshortt.blockchainbank.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
}
