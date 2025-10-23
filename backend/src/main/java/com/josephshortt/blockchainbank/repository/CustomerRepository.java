package com.josephshortt.blockchainbank.repository;

import com.josephshortt.blockchainbank.models.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerAccount, Long> {
    //Custom queries to add...
}
