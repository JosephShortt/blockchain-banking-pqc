package com.josephshortt.blockchainbank.blockchain;

import com.josephshortt.blockchainbank.crypto.PQCService;
import com.josephshortt.blockchainbank.repository.BankKeysRepository;
import com.josephshortt.blockchainbank.repository.BlockRepository;
import com.josephshortt.blockchainbank.repository.BlockTransactionRepository;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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

    @Value("${bank.if}")
    private String bankId;

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
        String data = block.getPrevHash() + block.getBlockNumber() + block.getMerkleRoot() + block.getCreatedAt();
        return pqcService.hashSHA256(data);
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

        for(BlockTransaction tx : pendingTransactions){
            tx.setBlock(newBlock);
        }
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
            String data = tx.getSenderIban() + tx.getReceiverIban() + tx.getAmount().toString() + tx.getSenderBankId() + tx.getReceiverBankId();
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
        String data = block.getBlockNumber() + block.getPrevHash() +block.getProposerId() + block.getCreatedAt() + block.getMerkleRoot();
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

    public boolean validateBlock(Block block){

        return false;
    }

    public boolean validateChain(){

        return false;
    }

    /*
    Settlement methods (After consensus)
    1. Process Incoming transactions
    2. Calculate Net settlement
    3. Settle Bank reserves
     */

    public void processIncomingTransactions(Block finalizedBlock){

    }

    public Map<String, BigDecimal> calculateNetPositions(Block block){

        return Map.of();
    }

    public void settleBankReserves(Block block){}

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
        return Optional.empty();
    }
}
