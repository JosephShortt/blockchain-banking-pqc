package com.josephshortt.blockchain.tests.crypto;

import com.josephshortt.blockchainbank.crypto.PQCService;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

public class PostQuantumCrpytoServiceTest {

    @Test
    public void testDilithiumSignatureWorks() throws Exception {
        PQCService pqc = new PQCService();

        // Generate keys
        KeyPair keys = pqc.generateDilithiumKeyPair();
        assertNotNull(keys);

        // Sign data
        String data = "Test transaction";
        String signature = pqc.signDilithium(data, keys.getPrivate());
        assertNotNull(signature);
        // Verify signature
        boolean valid = pqc.verifyDilithium(data, signature, keys.getPublic());
        assertTrue(valid);

        System.out.println("✓ PQC test passed!");
    }
}
