package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.models.ReserveAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveAccountRepository extends JpaRepository<ReserveAccount, String> {
}
