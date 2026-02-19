package com.josephshortt.blockchainbank.blockchain;

import com.josephshortt.blockchainbank.crypto.PQCService;
import com.josephshortt.blockchainbank.repository.BlockRepository;
import com.josephshortt.blockchainbank.repository.BlockTransactionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BlockchainService {
    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockTransactionRepository blockTransactionRepository;

    @Autowired
    private PQCService pqcService;

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

        return List.of();
    }

    /*
    Block creation methods
    1. createBlock()
    2. calculate merkle root from lis of transactions
    3. sign block
     */

    public void createBlock(){

    }


    private String calculateMerkleRoot(List<BlockTransaction> transactions){

        return "";
    }


    private String signBlock(Block block){

        return "";
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
