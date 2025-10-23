package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.models.CustomerAccount;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<DefaultBankAccount, Long> {
    Optional<DefaultBankAccount> findByIban(String iban);
}
