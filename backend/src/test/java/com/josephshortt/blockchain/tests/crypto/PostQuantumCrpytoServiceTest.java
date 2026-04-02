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

    // 1. Tampered signature should fail verification
    @Test
    public void testTamperedSignatureFailsVerification() throws Exception {
        PQCService pqc = new PQCService();
        KeyPair keys = pqc.generateDilithiumKeyPair();
        String data = "Test transaction";
        String signature = pqc.signDilithium(data, keys.getPrivate());

        boolean valid = pqc.verifyDilithium("Tampered data", signature, keys.getPublic());
        assertFalse(valid);
    }

    // 2. Wrong key should fail verification
    @Test
    public void testWrongKeyFailsVerification() throws Exception {
        PQCService pqc = new PQCService();
        KeyPair keys1 = pqc.generateDilithiumKeyPair();
        KeyPair keys2 = pqc.generateDilithiumKeyPair();
        String signature = pqc.signDilithium("data", keys1.getPrivate());

        boolean valid = pqc.verifyDilithium("data", signature, keys2.getPublic());
        assertFalse(valid);
    }

    // 3. SHA-256 hash consistency
    @Test
    public void testHashIsConsistent() throws Exception {
        PQCService pqc = new PQCService();
        String hash1 = pqc.hashSHA256("same data");
        String hash2 = pqc.hashSHA256("same data");
        assertEquals(hash1, hash2);
    }

    // 4. Different data produces different hash
    @Test
    public void testDifferentDataProducesDifferentHash() throws Exception {
        PQCService pqc = new PQCService();
        String hash1 = pqc.hashSHA256("data1");
        String hash2 = pqc.hashSHA256("data2");
        assertNotEquals(hash1, hash2);
    }
}
