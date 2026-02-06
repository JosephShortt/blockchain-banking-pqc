package com.josephshortt.blockchainbank.crypto;

import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

@Service
public class PostQuantumCryptoService {

    static{
        Security.addProvider(new BouncyCastlePQCProvider());
    }

    //Generate dilithium key pair
    public KeyPair generateDilithiumKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("Dilithium","BCPQC");
        keyGen.initialize(DilithiumParameterSpec.dilithium3);
        return keyGen.generateKeyPair();
    }

    //Sign data with dilithium private key
    public String signDilithium(String data, PrivateKey privateKey) throws Exception{
        Signature sig = Signature.getInstance("Dilithium", "BCPQC");
        sig.initSign(privateKey);
        sig.update(data.getBytes());
        byte[] signatureBytes = sig.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
    //Verify signature
    public boolean verifyDilithium(String data, String signatureBase64, PublicKey publicKey) throws Exception{
        Signature sig = Signature.getInstance("Dilithium", "BCPQC");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        return sig.verify(signatureBytes);
    }

    public String hashSHA256(String data) throws Exception{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

}
