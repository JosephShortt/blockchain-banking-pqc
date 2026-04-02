package com.josephshortt.blockchainbank;

import com.josephshortt.blockchainbank.controllers.TransactionController;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import com.josephshortt.blockchainbank.models.TransactionRequest;
import com.josephshortt.blockchainbank.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TransactionSimulator {

    @Autowired
    private TransactionController transactionController;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Value("${bank.id}")
    private String bankId;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final List<String[]> demoAccounts = Arrays.asList(
            new String[]{"IE29BANKA00004", "demo1@banka.com", "demo", "bank-a"},
            new String[]{"IE29BANKA00005", "demo2@banka.com", "demo", "bank-a"},
            new String[]{"IE29BANKA00006", "demo3@banka.com", "demo", "bank-a"},
            new String[]{"IE29BANKB00005", "demo1@bankb.com", "demo", "bank-b"},
            new String[]{"IE29BANKB00006", "demo2@bankb.com", "demo", "bank-b"},
            new String[]{"IE29BANKB00007", "demo3@bankb.com", "demo", "bank-b"},
            new String[]{"IE29BANKC00003", "demo1@bankc.com", "demo", "bank-c"},
            new String[]{"IE29BANKC00004", "demo2@bankc.com", "demo", "bank-c"},
            new String[]{"IE29BANKC00005", "demo3@bankc.com", "demo", "bank-c"}
    );

    private final Random random = new Random();

    public void start() {
        running.set(true);
        System.out.println("Transaction simulator started");
    }

    public void stop() {
        running.set(false);
        System.out.println("Transaction simulator stopped");
    }

    public boolean isRunning() {
        return running.get();
    }

    @Scheduled(fixedDelay = 15000)
    public void simulateTransaction() {
        if (!running.get()) return;

        try {
            // Pick random sender from this bank's accounts
            List<String[]> localAccounts = demoAccounts.stream()
                    .filter(a -> a[3].equals(bankId))
                    .toList();

            if (localAccounts.isEmpty()) return;

            String[] sender = localAccounts.get(random.nextInt(localAccounts.size()));

            // Pick random receiver from a different bank
            List<String[]> otherAccounts = demoAccounts.stream()
                    .filter(a -> !a[3].equals(bankId))
                    .toList();

            String[] receiver = otherAccounts.get(random.nextInt(otherAccounts.size()));

            // Random amount between 5 and 100
            double amount = 5 + random.nextInt(96);

            // Check sender has enough balance
            Optional<DefaultBankAccount> senderAccount = bankAccountRepository.findByIban(sender[0]);
            if (senderAccount.isEmpty() || senderAccount.get().getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
                System.out.println("Simulator: insufficient balance for " + sender[0]);
                return;
            }

            // Build request
            TransactionRequest request = new TransactionRequest();
            request.setAccount(senderAccount.get());
            request.setIban(receiver[0]);
            request.setAmount(amount);
            request.setPassword(sender[2]);

            transactionController.SendMoney(request);

            System.out.println("Simulator: " + sender[0] + " → " + receiver[0] + " €" + amount);

        } catch (Exception e) {
            System.err.println("Simulator error: " + e.getMessage());
        }
    }
}