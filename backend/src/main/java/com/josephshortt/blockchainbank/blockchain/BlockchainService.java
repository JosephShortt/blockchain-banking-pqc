package com.josephshortt.blockchainbank.blockchain;

import com.josephshortt.blockchainbank.consensus.ConsensusService;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.math.BigDecimal;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    @Lazy
    private ConsensusService consensusService;

    @Value("${BANK_A_URL:https://localhost:8443}")
    private String bankAUrl;

    @Value("${BANK_B_URL:http://localhost:8444}")
    private String bankBUrl;

    @Value("${BANK_C_URL:http://localhost:8445}")
    private String bankCUrl;

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
            genesis.setMerkleRoot("");
            genesis.setCreatedAt(LocalDateTime.parse("2026-01-01T00:00:00.000000"));
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
        String[] banks = {"bank-a", "bank-b", "bank-c"};

        for(String bankId : banks){
            SecureRandom seededRandom = SecureRandom.getInstance("SHA1PRNG");
            seededRandom.setSeed(bankId.getBytes());

            KeyPair keys = pqcService.generateDilithiumKeyPair(seededRandom);

            String publicKey = Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded());

            BankKeys bankKeys = new BankKeys();
            bankKeys.setBankId(bankId);
            bankKeys.setBankPublicKey(publicKey);
            bankKeys.setBankPrivateKey(privateKey);

            bankKeysRepository.save(bankKeys);
            System.out.println("Generated Keys for " + bankId);
        }
    }

    //Calculate hash of the current block using its data - prevHash, block number, merkle root ...
    public String calculateHash(Block block) throws Exception {
        return pqcService.hashSHA256(getBlockDataString(block));
    }

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    private String getBlockDataString(Block block) {
        return block.getBlockNumber() +
                block.getPrevHash() +
                block.getProposerId() +
                block.getMerkleRoot() +
                block.getCreatedAt().format(TIMESTAMP_FORMATTER);
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
        // Check if it's our turn to propose
        Block latestBlock = getLatestBlock().orElseThrow();
        Long nextBlockNumber = latestBlock.getBlockNumber() + 1;


        String[] bankOrder = {"bank-a", "bank-b", "bank-c"};
        String expectedProposer = bankOrder[(int)((nextBlockNumber - 1) % 3)];

        if (!expectedProposer.equals(bankId)) {
            return;
        }

        // Check previous block is finalized
        if (latestBlock.getStatus() != BlockStatus.FINALIZED) {
            System.out.println("Cannot create block " + nextBlockNumber +
                    " - block " + latestBlock.getBlockNumber() + " not finalized yet");
            return;
        }

        // Fetch pending transactions from all banks
        List<BlockTransaction> ownPendingTransactions = new ArrayList<>(getPendingTransactions());

        List<BlockTransaction> allTransactions = new ArrayList<>(ownPendingTransactions);

        String[] allBankUrls = {bankAUrl, bankBUrl, bankCUrl};
        for (String url : allBankUrls) {
            if (!url.equals(getOwnUrl())) {  // skip own bank
                List<BlockTransaction> remoteTxs = fetchPendingFromBank(url);
                for (BlockTransaction tx : remoteTxs) {
                    tx.setTxId(null);
                }
                allTransactions.addAll(remoteTxs);
            }
        }



        if (allTransactions.isEmpty()) {
            return;
        }

        // Build the block
        Block newBlock = new Block();
        newBlock.setBlockNumber(nextBlockNumber);
        newBlock.setPrevHash(latestBlock.getHash());
        newBlock.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        newBlock.setProposerId(bankId);
        newBlock.setStatus(BlockStatus.PROPOSED);

        String root = calculateMerkleRoot(allTransactions);
        newBlock.setMerkleRoot(root);

        String hash = calculateHash(newBlock);
        newBlock.setHash(hash);

        System.out.println("=== CREATING BLOCK ===");
        System.out.println("Block number: " + newBlock.getBlockNumber());
        System.out.println("Prev hash: " + newBlock.getPrevHash());
        System.out.println("Proposer: " + newBlock.getProposerId());
        System.out.println("Merkle root: " + newBlock.getMerkleRoot());
        System.out.println("Created at: " + newBlock.getCreatedAt());
        System.out.println("Calculated hash: " + hash);

        String signature = signBlock(newBlock);
        newBlock.setBlockSignature(signature);

        blockRepository.save(newBlock);

        for (BlockTransaction tx : ownPendingTransactions) {
            tx.setBlock(newBlock);
        }
        blockTransactionRepository.saveAll(ownPendingTransactions);
        newBlock.setTransactions(allTransactions);

        System.out.println("Block " + newBlock.getBlockNumber() + " created with " + allTransactions.size() + " transactions");

        consensusService.initiateConsensus(newBlock);
    }

    private String getOwnUrl() {
        if (bankId.equals("bank-a")) return bankAUrl;
        if (bankId.equals("bank-b")) return bankBUrl;
        return bankCUrl;
    }

    private List<BlockTransaction> fetchPendingFromBank(String bankUrl) {
        try {
            RestTemplate rt = createTrustAllRestTemplate();
            BlockTransaction[] txs = rt.getForObject(bankUrl + "/api/blockchain/pending-transactions", BlockTransaction[].class);
            if (txs != null) {
                return new ArrayList<>(Arrays.asList(txs));
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch pending transactions from " + bankUrl + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private RestTemplate createTrustAllRestTemplate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            return new RestTemplate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RestTemplate", e);
        }
    }


    public void markLocalTransactionsAsProcessed(Block block) {
        System.out.println("=== MARKING LOCAL TRANSACTIONS AS PROCESSED ===");
        List<BlockTransaction> pending = blockTransactionRepository.findByBlockIsNull();
        System.out.println("Pending transactions found: " + pending.size());

        for (BlockTransaction localTx : pending) {
            localTx.setBlock(block);
            blockTransactionRepository.save(localTx);
            System.out.println("Marked tx " + localTx.getTxId() + " as processed in block " + block.getBlockNumber());
        }
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
        System.out.println("Block data being signed: " + data);

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
            System.out.println("Block number: " + block.getBlockNumber());
            System.out.println("Transaction count: " + block.getTransactions().size());
            System.out.println("Calculated merkle: " + calculatedMerkle);
            System.out.println("Stored merkle:     " + block.getMerkleRoot());

            // Debug each transaction
            for(BlockTransaction tx : block.getTransactions()) {
                String txData = tx.getSenderIban() + tx.getReceiverIban() +
                        tx.getAmount().toPlainString() +
                        tx.getSenderBankId() + tx.getReceiverBankId();
                System.out.println("  Tx data: " + txData);
            }

            return false;
        }

        //Check if valid hash
        String hash = calculateHash(block);
        if(!hash.equals(block.getHash())){
            System.out.println("*** Validation Failure: Block hash invalid***");
            System.out.println("Block number: " + block.getBlockNumber());
            System.out.println("Calculated hash: " + hash);
            System.out.println("Stored hash:     " + block.getHash());

            // Debug: show the data being hashed
            System.out.println("Block data for hash:");
            System.out.println("  blockNumber: " + block.getBlockNumber());
            System.out.println("  prevHash: " + block.getPrevHash());
            System.out.println("  proposerId: " + block.getProposerId());
            System.out.println("  merkleRoot: " + block.getMerkleRoot());
            System.out.println("  createdAt: " + block.getCreatedAt());

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
        System.out.println("Block data being verified: " + blockData);
        System.out.println("Block signature length: " + block.getBlockSignature().length());

        if(!pqcService.verifyDilithium(blockData,block.getBlockSignature(), proposerPublicKey)){

            System.out.println("*** Validation Failure:Block signature invalid***");
            return false;
        }

        for(BlockTransaction tx : block.getTransactions()){
            if(!validateTransactionSignature(tx)){
                System.out.println("*** Validation Failure: Transaction signature invalid***");
                System.out.println(tx.getSenderSignature());
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

        System.out.println("=== VERIFYING TRANSACTION SIGNATURE ===");
        System.out.println("Sender IBAN: '" + tx.getSenderIban() + "'");
        System.out.println("Receiver IBAN: '" + tx.getReceiverIban() + "'");
        System.out.println("Amount: '" + tx.getAmount().toPlainString() + "'");
        System.out.println("Sender Bank: '" + tx.getSenderBankId() + "'");
        System.out.println("Receiver Bank: '" + tx.getReceiverBankId() + "'");
        System.out.println("Combined txData: '" + txData + "'");
        System.out.println("Data length: " + txData.length());
        System.out.println("Signature: " + tx.getSenderSignature().substring(0, 30) + "...");

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

    public void processIncomingTransactions(Block finalizedBlock, List<BlockTransaction> transactions){

        List<BlockTransaction> incomingTxs = transactions
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
        List<BlockTransaction> allTxs = blockTransactionRepository.findByBlockBlockNumber(block.getBlockNumber());
        Map<String, BigDecimal> netPositions = new HashMap<>();

        for (BlockTransaction tx : allTxs) {
            netPositions.merge(tx.getSenderBankId(), tx.getAmount().negate(), BigDecimal::add);
            netPositions.merge(tx.getReceiverBankId(), tx.getAmount(), BigDecimal::add);

        }

        return netPositions;
    }

    public void settleBankReserves(Block block, List<BlockTransaction> transactions) {
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


    public Optional<Block> getBlockByNumber(Long blockNumber){
        return blockRepository.findByBlockNumber(blockNumber);
    }
}
