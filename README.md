# Blockchain Banking PQC - Final Year Project

## File Structure
```
│   README.md
│    
├───.github
│   └───workflows
│           ci.yml
│           deploy.yml
│           
├───backend
│   │   .gitignore
│   │   Dockerfile
│   │   pom.xml
│   │   
│   ├───.mvn
│   ├───src
│   │   ├───main
│   │   │   ├───java
│   │   │   │   └───com
│   │   │   │       └───josephshortt
│   │   │   │           └───blockchainbank
│   │   │   │               │   Main.java
│   │   │   │               │   
│   │   │   │               ├───config
│   │   │   │               │       CorsConfig.java
│   │   │   │               │       
│   │   │   │               ├───controllers
│   │   │   │               │       CustomerAccountController.java
│   │   │   │               │       LoginController.java
│   │   │   │               │       TransactionController.java
│   │   │   │               │       TransactionsController.java
│   │   │   │               │       
│   │   │   │               ├───models
│   │   │   │               │       AccountResponse.java
│   │   │   │               │       AccountType.java
│   │   │   │               │       CustomerAccount.java
│   │   │   │               │       DefaultBankAccount.java
│   │   │   │               │       HashPassword.java
│   │   │   │               │       LoadBankAccounts.java
│   │   │   │               │       LoadCustomerAccounts.java
│   │   │   │               │       LoginResponse.java
│   │   │   │               │       Transaction.java
│   │   │   │               │       TransactionRequest.java
│   │   │   │               │       
│   │   │   │               └───repository
│   │   │   │                       BankAccountRepository.java
│   │   │   │                       CustomerRepository.java
│   │   │   │                       TransactionRepository.java
│   │   │   │                       
│   │   │   └───resources
│   │   │           application.properties
│   │   │           
│   │   └───test
│   │       └───java
│   │           └───com
│   │               └───josephshortt
│   │                   └───blockchain
│   │                       └───tests
│   │                               LoadBankAccountsTest.java
│   │                               LoadCustomerAccounts.java
│   │                               
│   └───target
│       │   backend-0.0.1-SNAPSHOT.jar
│       │   backend-0.0.1-SNAPSHOT.jar.original
│       │   
│       ├───classes
│                 application.properties
│                           
│                               
└───frontend
    │   .env.development
    │   .env.production
    │   .gitignore
    │   Dockerfile
    │   package-lock.json
    │   package.json
    │   README.md
    │   
    │       
    └───src
        │   api.js
        │   App.css
        │   App.js
        │   App.test.js
        │   index.css
        │   index.js
        │   
        ├───components
        │       AccountCreations.js
        │       Home.js
        │       UserLogin.js
        │       
        └───contexts
                UserContext.js
                
```