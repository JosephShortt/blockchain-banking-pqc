package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.blockchain.BlockTransaction;
import com.josephshortt.blockchainbank.crypto.KeyManagementService;
import com.josephshortt.blockchainbank.models.*;
import com.josephshortt.blockchainbank.repository.BankAccountRepository;
import com.josephshortt.blockchainbank.repository.BlockTransactionRepository;
import com.josephshortt.blockchainbank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class CustomerAccountController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    //key generation and storage
    @Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    BlockTransactionRepository blockTransactionRepository;

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody CustomerAccount customerAccount) throws Exception {

        if(customerAccount.getFirstName().isEmpty() || customerAccount.getSurname().isEmpty()
                || customerAccount.getEmail().isEmpty() || customerAccount.getPassword().isEmpty()
                || customerAccount.getBankId() == null || customerAccount.getBankId().isEmpty()){
            return ResponseEntity.status(401).body("Please fill in all fields!!");
        }


        // Check if email already exists
        Optional<CustomerAccount> existingCustomer = customerRepository.findByEmail(customerAccount.getEmail());
        if(existingCustomer.isPresent()) {
            return ResponseEntity.status(401).body("Email is already in use");
        }

        //Generate and store user keypair
        keyManagementService.generateAndStoreKeys(customerAccount,customerAccount.getPassword());

        //Hash the passed password
        HashPassword hash = new HashPassword(customerAccount.getPassword());
        String hashedPassword = hash.digestAndEncode(customerAccount.getPassword());
        customerAccount.setPassword(hashedPassword);

        customerRepository.save(customerAccount);
        LoadCustomerAccounts.accounts.add(customerAccount);

        // Generate IBAN with bank code
        String iban = generateIban(customerAccount.getBankId(), customerAccount.getCustomerId());


        //Create default bank account for created account
        DefaultBankAccount defaultBankAccount = new DefaultBankAccount(
                customerAccount.getCustomerId(),
                "A"+customerAccount.getCustomerId(),
                iban,
                AccountType.CURRENT,
                new BigDecimal("1000.00"),
                customerAccount.getBankId()
        );


        bankAccountRepository.save(defaultBankAccount);
        LoadBankAccounts.defaultBankAccounts.add(defaultBankAccount);

        System.out.println("Created customer: " + customerAccount.getFirstName()+ " at " + customerAccount.getBankId());


        return ResponseEntity.ok(customerAccount);


    }

    private String generateIban(String bankId, Long customerId) {
        String bankCode;
        switch (bankId) {
            case "bank-a": bankCode = "BANKA"; break;
            case "bank-b": bankCode = "BANKB"; break;
            case "bank-c": bankCode = "BANKC"; break;
            default: bankCode = "BANKX";
        }

        return String.format("IE29%s%05d", bankCode, customerId);
        // Example: IE29BANKA00001, IE29BANKB00002, etc.
    }

    @GetMapping
    public List<CustomerAccount> getAllAccounts() {
        return LoadCustomerAccounts.accounts;
    }

    @GetMapping("/balance/{iban}")
    public ResponseEntity<?> getBalance(@PathVariable String iban) {
        Optional<DefaultBankAccount> account = bankAccountRepository.findByIban(iban);
        if (account.isEmpty()) {
            return ResponseEntity.status(404).body("Account not found");
        }
        return ResponseEntity.ok(account.get().getBalance());
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestBody Map<String, Object> request) {
        String iban = (String) request.get("iban");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        Optional<DefaultBankAccount> account = bankAccountRepository.findByIban(iban);
        if (account.isEmpty()) {
            return ResponseEntity.status(404).body("Account not found for refund");
        }

        DefaultBankAccount acc = account.get();
        acc.setBalance(acc.getBalance().add(amount));
        bankAccountRepository.save(acc);

        System.out.println("Refunded " + amount + " to " + iban);
        return ResponseEntity.ok("Refund processed");
    }

    @GetMapping("/transactions/block")
    public ResponseEntity<?> getTransactionBlock(
            @RequestParam String senderIban,
            @RequestParam String receiverIban,
            @RequestParam BigDecimal amount) {

        Optional<BlockTransaction> blockTx = blockTransactionRepository
                .findBySenderIbanAndReceiverIbanAndAmount(senderIban, receiverIban, amount);

        if (blockTx.isEmpty() || blockTx.get().getBlock() == null) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(blockTx.get().getBlock().getBlockNumber());
    }
}
