package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.models.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerAccount, Long> {
    Optional<CustomerAccount> findByEmail(String email);
}
