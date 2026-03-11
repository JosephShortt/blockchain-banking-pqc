package com.josephshortt.blockchain.tests;

import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import com.josephshortt.blockchainbank.models.LoadBankAccounts;
import com.josephshortt.blockchainbank.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadBankAccountsTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private LoadBankAccounts loadBankAccounts;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LoadBankAccounts.defaultBankAccounts.clear();
    }

    @Test
    void testLoadBankAccounts() {
        DefaultBankAccount acc = new DefaultBankAccount();
        acc.setCustomerId(99L);
        acc.setBalance(BigDecimal.valueOf(500.00));

        when(bankAccountRepository.findAll()).thenReturn(List.of(acc));

        loadBankAccounts.loadBankAccounts();

        assertEquals(1, LoadBankAccounts.defaultBankAccounts.size());
        assertEquals(500.00, LoadBankAccounts.defaultBankAccounts.get(0).getBalance());

        verify(bankAccountRepository, times(1)).findAll();
    }
}
