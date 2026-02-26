package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.blockchain.BlockTransaction;
import com.josephshortt.blockchainbank.crypto.KeyManagementService;
import com.josephshortt.blockchainbank.crypto.PQCService;
import com.josephshortt.blockchainbank.models.*;
import com.josephshortt.blockchainbank.repository.BankAccountRepository;
import com.josephshortt.blockchainbank.repository.BlockTransactionRepository;
import com.josephshortt.blockchainbank.repository.CustomerRepository;
import com.josephshortt.blockchainbank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/api/accounts/transaction")

public class TransactionController {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    private PQCService pqcService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BlockTransactionRepository blockTransactionRepository;

    @PostMapping
    public  ResponseEntity<?>  SendMoney(@RequestBody TransactionRequest request) throws Exception {

        DefaultBankAccount senderAccount = request.getAccount();

        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        String receiverIban = request.getIban();

        String receiverBankId = extractBankIdFromIban(receiverIban);

        //Inner bank transaction
        if(senderAccount.getBankId().equals(receiverBankId)){
            // INTERNAL TRANSFER - validate receiver exists
            Optional<DefaultBankAccount> optionalReceiver = bankAccountRepository.findByIban(receiverIban);

            if (optionalReceiver.isEmpty()) {
                return ResponseEntity.status(404).body("Account not found");
            }

            DefaultBankAccount receiverAccount = optionalReceiver.get();

            if(senderAccount.getBalance().compareTo(amount) >=0){

                // Update balances
                senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
                receiverAccount.setBalance(receiverAccount.getBalance().add(amount));


                // Save changes to DB
                bankAccountRepository.save(senderAccount);
                bankAccountRepository.save(receiverAccount);


                // Record transaction
                Transaction transaction = new Transaction(senderAccount.getIban(), receiverAccount.getIban(), amount, LocalDateTime.now());
                transaction.setTransactionType(TransactionType.INTERNAL);
                transactionRepository.save(transaction);


                return ResponseEntity.ok(senderAccount);
            }
        }
        //Interbank transaction
        else{

            if (!isValidIbanFormat(receiverIban)) {
                return ResponseEntity.status(400).body("Invalid IBAN format. Expected format: IE29BANK[A/B/C]#####");
            }

            if(senderAccount.getBalance().compareTo(amount) >=0){
                senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
                bankAccountRepository.save(senderAccount);

                Transaction localTx = new Transaction(
                        senderAccount.getIban(),
                        receiverIban,
                        amount,LocalDateTime.now()
                );

                localTx.setTransactionType(TransactionType.EXTERNAL);
                transactionRepository.save(localTx);

                //Get user password to decrypt and use users private key
                String userPassword = request.getPassword();
                Long customerId = senderAccount.getCustomerId();

                CustomerAccount customerAccount = customerRepository.findById(customerId).orElseThrow();

                //Get encrypted key of customer from DB
                String encryptedKey = customerAccount.getEncryptedPrivateKey();
                String publicKey = customerAccount.getPublicKey();

                //call key management to decrypt key
                PrivateKey privateKey = keyManagementService.decryptPrivateKey(encryptedKey,userPassword);

                //Create transaction data to sign
                String txData = senderAccount.getIban()+
                        receiverIban+
                        amount.toPlainString()+
                        senderAccount.getBankId() +
                        receiverBankId;

                String signature = pqcService.signDilithium(txData,privateKey);

                //Create block transaction and save to database
                BlockTransaction blockTx = new BlockTransaction();

                blockTx.setSenderIban(senderAccount.getIban());
                blockTx.setReceiverIban(receiverIban);
                blockTx.setAmount(amount);
                blockTx.setSenderBankId(senderAccount.getBankId());
                blockTx.setReceiverBankId(receiverBankId);
                blockTx.setSenderSignature(signature);
                blockTx.setSenderPublicKey(publicKey);

                blockTransactionRepository.save(blockTx);

                return ResponseEntity.ok(senderAccount);
            }
        }

        return ResponseEntity.status(400).body("Insufficient Funds");

    }

    private String extractBankIdFromIban(String iban) {
        // IE29BANKA00001 → "bank-a"
        // IE29BANKB00002 → "bank-b"
        // IE29BANKC00003 → "bank-c"

        if (iban.contains("BANKA")) return "bank-a";
        if (iban.contains("BANKB")) return "bank-b";
        if (iban.contains("BANKC")) return "bank-c";

        return "unknown";
    }

    private boolean isValidIbanFormat(String iban){
        if(iban == null || iban.isEmpty()){
            return false;
        }

        return iban.matches("IE29BANK[ABC]\\d{5}");
    }

}
