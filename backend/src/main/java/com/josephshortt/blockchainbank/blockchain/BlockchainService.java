package com.josephshortt.blockchainbank.blockchain;

import com.josephshortt.blockchainbank.crypto.PQCService;
import com.josephshortt.blockchainbank.repository.BlockRepository;
import com.josephshortt.blockchainbank.repository.BlockTransactionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockchainService {
    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockTransactionRepository blockTransactionRepository;

    @Autowired
    private PQCService pqcService;

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
}
