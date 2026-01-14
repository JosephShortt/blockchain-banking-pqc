    package com.josephshortt.blockchainbank.models;

    import com.josephshortt.blockchainbank.repository.BankAccountRepository;
    import jakarta.annotation.PostConstruct;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;

    import java.util.ArrayList;
    import java.util.List;

    @Component
    public class LoadBankAccounts {
        public static final List<DefaultBankAccount> defaultBankAccounts = new ArrayList<>();

        @Autowired
        private BankAccountRepository bankAccountRepository;

        @PostConstruct
        public void loadBankAccounts(){

            List<DefaultBankAccount> allAccounts = bankAccountRepository.findAll();
            defaultBankAccounts.clear();
            defaultBankAccounts.addAll(allAccounts);
            System.out.println("********* **********");
            System.out.println("Loaded " + defaultBankAccounts.size() + " bank accounts from database.");
            for(DefaultBankAccount account : defaultBankAccounts){
                System.out.println("ID: " + account.getCustomerId() + " Balance: " + account.getBalance());

            }
        }

        public List<DefaultBankAccount> getAccounts() {
            return defaultBankAccounts;
        }

    }
