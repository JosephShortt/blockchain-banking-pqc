package com.josephshortt.blockchain.tests;

import com.josephshortt.blockchainbank.models.CustomerAccount;
import com.josephshortt.blockchainbank.models.LoadCustomerAccounts;
import com.josephshortt.blockchainbank.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LoadCustomerAccountsTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private LoadCustomerAccounts loadCustomerAccounts;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LoadCustomerAccounts.accounts.clear(); // reset static list
    }

    @Test
    void testLoadAccountsLoadsFromRepository() {
        CustomerAccount c1 = new CustomerAccount();
        c1.setCustomerId(1L);
        c1.setFirstName("Alice");

        CustomerAccount c2 = new CustomerAccount();
        c2.setCustomerId(2L);
        c2.setFirstName("Bob");

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        loadCustomerAccounts.loadAccounts();

        assertEquals(2, LoadCustomerAccounts.accounts.size());
        assertEquals("Alice", LoadCustomerAccounts.accounts.get(0).getFirstName());

        verify(customerRepository, times(1)).findAll();
    }
}
