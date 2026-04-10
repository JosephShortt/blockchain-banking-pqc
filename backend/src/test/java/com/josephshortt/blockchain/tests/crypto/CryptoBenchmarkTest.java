package com.josephshortt.blockchain.tests.crypto;

import com.josephshortt.blockchainbank.crypto.PQCService;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class CryptoBenchmarkTest {

    private static final int ITERATIONS = 1000;
    private static final String TEST_DATA = "IE29BANKA00002IE29BANKB000031000.00bank-abank-b";



    @Test
    public void benchmarkECDSASigning() throws Exception {
        // Warmup
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
        gen.initialize(256);
        KeyPair keys = gen.generateKeyPair();
        Signature signer = Signature.getInstance("SHA256withECDSA");

        for (int i = 0; i < 10; i++) {
            signer.initSign(keys.getPrivate());
            signer.update(TEST_DATA.getBytes());
            signer.sign();
        }

        // Benchmark
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            signer.initSign(keys.getPrivate());
            signer.update(TEST_DATA.getBytes());
            signer.sign();
            long end = System.nanoTime();
            times.add(end - start);
        }

        long avg = times.stream().mapToLong(Long::longValue).sum() / ITERATIONS;
        long min = times.stream().mapToLong(Long::longValue).min().getAsLong();
        long max = times.stream().mapToLong(Long::longValue).max().getAsLong();

        System.out.println("=== ECDSA (P-256) Signing Benchmark (" + ITERATIONS + " iterations) ===");
        System.out.println("Average: " + avg / 1_000_000.0 + " ms");
        System.out.println("Min:     " + min / 1_000_000.0 + " ms");
        System.out.println("Max:     " + max / 1_000_000.0 + " ms");
    }

    @Test
    public void benchmarkECDSAVerification() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
        gen.initialize(256);
        KeyPair keys = gen.generateKeyPair();
        Signature signer = Signature.getInstance("SHA256withECDSA");

        signer.initSign(keys.getPrivate());
        signer.update(TEST_DATA.getBytes());
        byte[] signature = signer.sign();

        // Warmup
        for (int i = 0; i < 10; i++) {
            Signature verifier = Signature.getInstance("SHA256withECDSA");
            verifier.initVerify(keys.getPublic());
            verifier.update(TEST_DATA.getBytes());
            verifier.verify(signature);
        }

        // Benchmark
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            Signature verifier = Signature.getInstance("SHA256withECDSA");
            verifier.initVerify(keys.getPublic());
            verifier.update(TEST_DATA.getBytes());
            verifier.verify(signature);
            long end = System.nanoTime();
            times.add(end - start);
        }

        long avg = times.stream().mapToLong(Long::longValue).sum() / ITERATIONS;
        long min = times.stream().mapToLong(Long::longValue).min().getAsLong();
        long max = times.stream().mapToLong(Long::longValue).max().getAsLong();

        System.out.println("=== ECDSA (P-256) Verification Benchmark (" + ITERATIONS + " iterations) ===");
        System.out.println("Average: " + avg / 1_000_000.0 + " ms");
        System.out.println("Min:     " + min / 1_000_000.0 + " ms");
        System.out.println("Max:     " + max / 1_000_000.0 + " ms");
    }

    @Test
    public void benchmarkKeyGeneration() throws Exception {
        PQCService pqc = new PQCService();

        // Dilithium key generation
        List<Long> dilithiumTimes = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            pqc.generateDilithiumKeyPair();
            long end = System.nanoTime();
            dilithiumTimes.add(end - start);
        }

        long dilithiumAvg = dilithiumTimes.stream().mapToLong(Long::longValue).sum() / ITERATIONS;

        // ECDSA key generation
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
        gen.initialize(256);
        List<Long> ecdsaTimes = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            gen.generateKeyPair();
            long end = System.nanoTime();
            ecdsaTimes.add(end - start);
        }

        long ecdsaAvg = ecdsaTimes.stream().mapToLong(Long::longValue).sum() / ITERATIONS;

        System.out.println("=== Key Generation Benchmark (" + ITERATIONS + " iterations) ===");
        System.out.println("Dilithium5 avg: " + dilithiumAvg / 1_000_000.0 + " ms");
        System.out.println("ECDSA P-256 avg: " + ecdsaAvg / 1_000_000.0 + " ms");
    }

    @Test
    public void benchmarkAllDilithiumLevels() throws Exception {
        PQCService pqc = new PQCService();

        DilithiumParameterSpec[] specs = {
                DilithiumParameterSpec.dilithium2,
                DilithiumParameterSpec.dilithium3,
                DilithiumParameterSpec.dilithium5
        };
        String[] names = {"Dilithium2", "Dilithium3", "Dilithium5"};

        for (int s = 0; s < specs.length; s++) {
            KeyPair keys = pqc.generateDilithiumKeyPair(specs[s]);
            String signature = pqc.signDilithium(TEST_DATA, keys.getPrivate());

            // Key generation
            List<Long> keyGenTimes = new ArrayList<>();
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.nanoTime();
                pqc.generateDilithiumKeyPair(specs[s]);
                keyGenTimes.add(System.nanoTime() - start);
            }

            // Signing
            List<Long> signTimes = new ArrayList<>();
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.nanoTime();
                pqc.signDilithium(TEST_DATA, keys.getPrivate());
                signTimes.add(System.nanoTime() - start);
            }

            // Verification
            List<Long> verifyTimes = new ArrayList<>();
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.nanoTime();
                pqc.verifyDilithium(TEST_DATA, signature, keys.getPublic());
                verifyTimes.add(System.nanoTime() - start);
            }

            long keyGenAvg = keyGenTimes.stream().mapToLong(Long::longValue).sum() / ITERATIONS;
            long signAvg = signTimes.stream().mapToLong(Long::longValue).sum() / ITERATIONS;
            long verifyAvg = verifyTimes.stream().mapToLong(Long::longValue).sum() / ITERATIONS;

            System.out.println("=== " + names[s] + " (" + ITERATIONS + " iterations) ===");
            System.out.println("Key Generation avg: " + keyGenAvg / 1_000_000.0 + " ms");
            System.out.println("Signing avg:        " + signAvg / 1_000_000.0 + " ms");
            System.out.println("Verification avg:   " + verifyAvg / 1_000_000.0 + " ms");
            System.out.println();
        }
    }
}