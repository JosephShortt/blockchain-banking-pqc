package com.josephshortt.blockchainbank.crypto;

import com.josephshortt.blockchainbank.models.CustomerAccount;
import com.josephshortt.blockchainbank.repository.CustomerRepository;
import org.bouncycastle.jcajce.provider.digest.MD2;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumKey;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Service
public class KeyManagementService {
    /*
    * KeyManagementService class is responsible for:
    * 1. Generating keys when user registers (dilithium public-private)
    * 2. Encrypting private key with user password
    * 3. Storing encrypted keys in DB
    * 4. Decrypting private key when user requests a transaction
    * */
    @Autowired
    private PQCService pqc;
    @Autowired
    private CustomerRepository customerRepository;

    public void generateAndStoreKeys(CustomerAccount user,String userPassword) throws Exception {
        KeyPair keys = pqc.generateDilithiumKeyPair();
        String encryptedPrivateKey = encrypt(keys.getPrivate(), userPassword);
        String publicKey = encodePublicKey(keys.getPublic());

        user.setEncryptedPrivateKey(encryptedPrivateKey);
        user.setPublicKey(publicKey);

        customerRepository.save(user);
    }

    //Encode public key to String for storing
    private String encodePublicKey(PublicKey pKey){
        return Base64.getEncoder().encodeToString(pKey.getEncoded());
    }

    //Decode public key when user wants to make a transaction
    public PublicKey decodePublicKey(String publicKeyString) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        byte[] bytes = Base64.getDecoder().decode(publicKeyString);

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytes);

        KeyFactory keyFactory = KeyFactory.getInstance("Dilithium","BCPQC");
        return keyFactory.generatePublic(publicKeySpec);
    }


    private String encrypt(PrivateKey privateKey, String userPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(userPassword.getBytes());
        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        // Encrypt the private key
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(privateKey.getEncoded());

        // Return as Base64 string
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public PrivateKey decryptPrivateKey(String encryptedPrivateKey, String userPassword) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPrivateKey);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(userPassword.getBytes());
        SecretKey key = new SecretKeySpec(keyBytes,"AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decryptedBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("Dilithium","BCPQC");
        return keyFactory.generatePrivate(privateKeySpec);
    }


}
