package com.josephshortt.blockchainbank.blockchain;

import com.josephshortt.blockchainbank.crypto.KeyManagementService;
import com.josephshortt.blockchainbank.crypto.PQCService;
import com.josephshortt.blockchainbank.models.DefaultBankAccount;
import com.josephshortt.blockchainbank.models.ReserveAccount;
import com.josephshortt.blockchainbank.models.Transaction;
import com.josephshortt.blockchainbank.models.TransactionType;
import com.josephshortt.blockchainbank.repository.*;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BlockchainService {
    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockTransactionRepository blockTransactionRepository;

    @Autowired
    private PQCService pqcService;

    @Autowired
    private BankKeysRepository bankKeysRepository;

    @Autowired
    private KeyManagementService keyManagementService;

    @Value("${bank.id}")
    private String bankId;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private ReserveAccountRepository reserveAccountRepository;

    /*
      Basic Block operations
      1. Initialise genesis block
      2. calculate block hash
      3. Get latest block in chain for creating new blocks
      4. get pending transactions for block creation
     */
    //If blockchain has not been initialised create the genesis block(block 1)
    @PostConstruct
    public void initialiseChain() throws Exception {
        if(blockRepository.count() == 0){
            Block genesis = new Block();
            genesis.setBlockNumber(0L);
            genesis.setPrevHash("0");
            genesis.setProposerId("System");
            genesis.setStatus(BlockStatus.FINALIZED);
            genesis.setHash(calculateHash(genesis));

            blockRepository.save(genesis);
            System.out.println("**Genesis Block Created**");
        }

        if(bankKeysRepository.count() == 0){
            generateBankKeys();
        }

        // Initialize bank reserves if not exist
        if (reserveAccountRepository.count() == 0) {
            initializeBankReserves();
        }
    }

    private void initializeBankReserves() {
        ReserveAccount bankA = new ReserveAccount(
                "bank-a",
                "Bank A",
                new BigDecimal("1000000.00")
        );

        ReserveAccount bankB = new ReserveAccount(
                "bank-b",
                "Bank B",
                new BigDecimal("1000000.00")
        );

        ReserveAccount bankC = new ReserveAccount(
                "bank-c",
                "Bank C",
                new BigDecimal("1000000.00")
        );

        reserveAccountRepository.save(bankA);
        reserveAccountRepository.save(bankB);
        reserveAccountRepository.save(bankC);

        System.out.println("Bank reserves initialized");
    }

    private void generateBankKeys() throws Exception {
        String[] banks = {"bank-a" , "bank-b", "bank-c"};

        for(String bankId : banks){
            KeyPair keys = pqcService.generateDilithiumKeyPair();

            String publicKey = Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded());

            BankKeys bankKeys = new BankKeys();
            bankKeys.setBankId(bankId);
            bankKeys.setBankPublicKey(publicKey);
            bankKeys.setBankPrivateKey(privateKey);

            bankKeysRepository.save(bankKeys);
            System.out.println("Generated Keys for "+bankId);
        }
    }

    //Calculate hash of the current block using its data - prevHash, block number, merkle root ...
    public String calculateHash(Block block) throws Exception {
        return pqcService.hashSHA256(getBlockDataString(block));
    }

    private String getBlockDataString(Block block) {
        return block.getBlockNumber() +
                block.getPrevHash() +
                block.getProposerId() +
                block.getMerkleRoot() +
                block.getCreatedAt();
    }


    public Optional<Block> getLatestBlock(){

        return blockRepository.findFirstByOrderByBlockNumberDesc();
    }

    //get pending transactions
    public List<BlockTransaction> getPendingTransactions(){

        return blockTransactionRepository.findByBlockIsNull();
    }

    /*
    Block creation methods
    1. createBlock()
    2. calculate merkle root from lis of transactions
    3. sign block
     */

    public void createBlock() throws Exception {
        List<BlockTransaction> pendingTransactions = getPendingTransactions();

        if(pendingTransactions.isEmpty()){
            System.out.println("No pending transactions: Skipping block creation");
            return;
        }
        Block prevBlock = getLatestBlock().orElseThrow();
        if(prevBlock.getStatus() != BlockStatus.FINALIZED){
            System.out.println("Cannot create block " + (prevBlock.getBlockNumber() + 1) +
                    " - block " + prevBlock.getBlockNumber() + " not finalized yet");
            return;
        }
        //Continue to create block if previous checks pass
        Block newBlock = new Block();

        //Set block number and previous hash
        newBlock.setBlockNumber(prevBlock.getBlockNumber()+1);
        newBlock.setPrevHash(prevBlock.getHash());

        //Set proposer ID
        newBlock.setProposerId(bankId);
        newBlock.setStatus(BlockStatus.PROPOSED);


        //Set merkle root
        String root = calculateMerkleRoot(pendingTransactions);
        newBlock.setMerkleRoot(root);

        //Set block hash
        String hash = calculateHash(newBlock);
        newBlock.setHash(hash);

        //Set signature
        String signature = signBlock(newBlock);
        newBlock.setBlockSignature(signature);

        blockRepository.save(newBlock);

        for(BlockTransaction tx : pendingTransactions){
            tx.setBlock(newBlock);
        }

        blockTransactionRepository.saveAll(pendingTransactions);

        System.out.println("Block " + newBlock.getBlockNumber() + " created with " + pendingTransactions.size() + " transactions");

    }


    private String calculateMerkleRoot(List<BlockTransaction> transactions) throws Exception {
        if(transactions ==null || transactions.isEmpty()){
            return pqcService.hashSHA256("empty");
        }

        List<String> txHashes = new ArrayList<>();

        //hash each transactions
        for(BlockTransaction tx : transactions){
            String data = tx.getSenderIban() + tx.getReceiverIban() + tx.getAmount().toPlainString() + tx.getSenderBankId() + tx.getReceiverBankId();
            String hash = pqcService.hashSHA256(data);
            txHashes.add(hash);
        }

        //Finally, hash the tx hashes into one hash

        StringBuilder combined = new StringBuilder();
        for(String hash : txHashes){
            combined.append(hash);
        }

        return pqcService.hashSHA256(combined.toString());
    }


    private String signBlock(Block block) throws Exception {
        String data = getBlockDataString(block);

        BankKeys keys = bankKeysRepository.findByBankId(block.getProposerId()).orElseThrow();
        byte[] bytes = Base64.getDecoder().decode(keys.getBankPrivateKey());

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);

        KeyFactory keyFactory = KeyFactory.getInstance("Dilithium", "BCPQC");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return pqcService.signDilithium(data,privateKey);
    }

      /*
    Validation and consensus
    1. validate block
    2. validate chain
    3. validate transaction sig

     */

    public boolean validateBlock(Block block) throws Exception {
        //Check if valid merkle root
        String calculatedMerkle = calculateMerkleRoot(block.getTransactions());
        if(!calculatedMerkle.equals(block.getMerkleRoot())){
            System.out.println("*** Validation Failure: Merkle tree invalid***");
            return false;
        }

        //Check if valid hash
        String hash = calculateHash(block);
        if(!hash.equals(block.getHash())){
            System.out.println("*** Validation Failure: Block hash invalid***");
            return false;
        }

        //Check if previous block hash is correct
        Block prevBlock = getBlockByNumber(block.getBlockNumber()-1).orElseThrow();
        if(!block.getPrevHash().equals(prevBlock.getHash())){
            System.out.println("*** Validation Failure: Previous block hash invalid***");
            return false;
        }

        //Verify proposer signed this block
        BankKeys proposerKeys = bankKeysRepository.findByBankId(block.getProposerId()).orElseThrow();
        PublicKey proposerPublicKey = keyManagementService.decodePublicKey(proposerKeys.getBankPublicKey());

        String blockData = getBlockDataString(block);

        if(!pqcService.verifyDilithium(blockData,block.getBlockSignature(), proposerPublicKey)){
            System.out.println("*** Validation Failure:Block signature invalid***");
            return false;
        }

        for(BlockTransaction tx : block.getTransactions()){
            if(!validateTransactionSignature(tx)){
                System.out.println("*** Validation Failure: Transaction signature invalid***");
                return false;
            }
        }
        return true;
    }

    public boolean validateTransactionSignature(BlockTransaction tx) throws Exception {
        PublicKey publicKey = keyManagementService.decodePublicKey(tx.getSenderPublicKey());

        String txData = tx.getSenderIban()+
                tx.getReceiverIban()+
                tx.getAmount().toPlainString()+
                tx.getSenderBankId()+
                tx.getReceiverBankId();

        String signature = tx.getSenderSignature();

        return pqcService.verifyDilithium(txData, signature, publicKey);
    }


    public boolean validateChain() throws Exception {
        // Get all finalized blocks in order
        List<Block> finalizedBlocks = blockRepository.findByStatusOrderByBlockNumberAsc(BlockStatus.FINALIZED);

        if (finalizedBlocks.isEmpty()) {
            return true;  // No blocks yet is valid
        }

        // Check starts with genesis
        if (finalizedBlocks.get(0).getBlockNumber() != 0L) {
            System.out.println("Chain doesn't start with genesis");
            return false;
        }

        // Validate sequence and integrity
        for (int i = 0; i < finalizedBlocks.size(); i++) {
            Block block = finalizedBlocks.get(i);

            // Check sequence
            if (block.getBlockNumber() != i) {
                System.out.println("Block gap at position " + i);
                return false;
            }

            // Validate block
            if (!validateBlock(block)) {
                System.out.println("Block " + i + " invalid");
                return false;
            }
        }

        System.out.println("Chain valid: " + finalizedBlocks.size() + " blocks");
        return true;
    }

    /*
    Settlement methods (After consensus)
    1. Process Incoming transactions
    2. Calculate Net settlement
    3. Settle Bank reserves
     */

    public void processIncomingTransactions(Block finalizedBlock){
        String currentBankId = bankId;

        List<BlockTransaction> incomingTxs = finalizedBlock.getTransactions()
                .stream()
                .filter(tx -> tx.getReceiverBankId().equals(bankId))
                .toList();

        System.out.println("Processing " + incomingTxs.size() + " incoming transactions");

        for(BlockTransaction tx : incomingTxs){
            Optional<DefaultBankAccount> receiverAccount = bankAccountRepository.findByIban(tx.getReceiverIban());

            if(receiverAccount.isEmpty()){
                System.out.println("Settlement Failed: Account "+ tx.getReceiverIban() + " not found.");
                // TODO: Notify sender's bank to refund
                continue;
            }

            DefaultBankAccount receiver = receiverAccount.get();
            receiver.setBalance(receiver.getBalance().add(tx.getAmount()));
            bankAccountRepository.save(receiver);

            //record local transactions table
            Transaction localTx = new Transaction(
                    tx.getSenderIban(),
                    tx.getReceiverIban(),
                    tx.getAmount(),
                    LocalDateTime.now()
            );

            localTx.setTransactionType(TransactionType.EXTERNAL);
            transactionRepository.save(localTx);

            System.out.println("Credited "+tx.getReceiverIban() + " with € "+tx.getAmount());
        }
    }

    public Map<String, BigDecimal> calculateNetPositions(Block block) {
        Map<String, BigDecimal> netPositions = new HashMap<>();

        for (BlockTransaction tx : block.getTransactions()) {
            String senderBank = tx.getSenderBankId();
            String receiverBank = tx.getReceiverBankId();
            BigDecimal amount = tx.getAmount();

            // Sender loses money
            netPositions.merge(senderBank, amount.negate(), BigDecimal::add);

            // Receiver gains money
            netPositions.merge(receiverBank, amount, BigDecimal::add);
        }

        return netPositions;
    }

    public void settleBankReserves(Block block) {
        // Calculate net positions for each bank
        Map<String, BigDecimal> netPositions = calculateNetPositions(block);

        System.out.println("=== Settling Bank Reserves for Block " + block.getBlockNumber() + " ===");

        // Update each bank's reserve balance
        for (Map.Entry<String, BigDecimal> entry : netPositions.entrySet()) {
            String bankId = entry.getKey();
            BigDecimal netAmount = entry.getValue();

            // Find the bank's reserve account
            Optional<ReserveAccount> optionalAccount = reserveAccountRepository.findById(bankId);

            if (optionalAccount.isEmpty()) {
                System.out.println("Reserve account not found for " + bankId);
                continue;
            }

            // Update reserves
            ReserveAccount account = optionalAccount.get();
            BigDecimal oldBalance = account.getReserveBalance();
            BigDecimal newBalance = oldBalance.add(netAmount);
            account.setReserveBalance(newBalance);
            reserveAccountRepository.save(account);

            // Log the change
            if (netAmount.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(bankId + " receives €" + netAmount +
                        " (reserves: €" + oldBalance + " → €" + newBalance + ")");
            } else if (netAmount.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println(bankId + " pays €" + netAmount.abs() +
                        " (reserves: €" + oldBalance + " → €" + newBalance + ")");
            } else {
                System.out.println("➖ " + bankId + " net zero (reserves: €" + newBalance + ")");
            }
        }

        // Verify total is zero (money conserved)
        BigDecimal total = netPositions.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total settlement: €" + total + " (should be €0.00)");
    }

    /*
    Utility Methods
    1. get current bank id
    2. Count blocks
    3. Get block by number
     */

    private String getCurrentBankId(){
        return "";
    }

    public long getBlockCount(){
        return 0;
    }

    public Optional<Block> getBlockByNumber(Long blockNumber){
        Block block = blockRepository.findByBlockNumber(blockNumber).orElseThrow();
        return Optional.of(block);
    }
}
