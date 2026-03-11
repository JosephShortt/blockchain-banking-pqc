package com.josephshortt.blockchainbank.consensus;

import com.josephshortt.blockchainbank.blockchain.Block;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class NetworkService {

    @Value("${bank.id}")
    private String bankId;

    @Value("${consensus.bank-a.url:https://localhost:8443}")
    private String bankAUrl;

    @Value("${consensus.bank-b.url:https://localhost:8444}")
    private String bankBUrl;

    @Value("${consensus.bank-c.url:https://localhost:8445}")
    private String bankCUrl;

    private RestTemplate restTemplate;

    public NetworkService() {
        // Create RestTemplate that accepts self-signed certificates
        this.restTemplate = createRestTemplate();
    }

    public void broadcastMessage(ConsensusMessage message, Block block) {
        List<String> otherBanks = getOtherBankUrls();

        for (String bankUrl : otherBanks) {
            try {
                String endpoint = getEndpointForMessageType(message.getMessageType());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Object payload;
                if (message.getMessageType() == ConsensusMessage.MessageType.PROPOSE && block != null) {
                    // For PROPOSE, send the full block
                    payload = new ProposePayload(message, block);
                } else {
                    // For PREPARE/COMMIT, just send the message
                    payload = message;
                }

                HttpEntity<Object> request = new HttpEntity<>(payload, headers);

                restTemplate.postForObject(bankUrl + endpoint, request, String.class);

                System.out.println("Sent " + message.getMessageType() + " to " + bankUrl);

            } catch (Exception e) {
                System.err.println("Failed to send message to " + bankUrl + ": " + e.getMessage());
            }
        }
    }

    private List<String> getOtherBankUrls() {
        List<String> allBanks = Arrays.asList(bankAUrl, bankBUrl, bankCUrl);
        List<String> otherBanks = new ArrayList<>();

        for (String url : allBanks) {
            // Don't send to ourselves
            if (!url.contains(getCurrentPort())) {
                otherBanks.add(url);
            }
        }

        return otherBanks;
    }

    private String getCurrentPort() {
        if (bankId.equals("bank-a")) return "8443";
        if (bankId.equals("bank-b")) return "8444";
        if (bankId.equals("bank-c")) return "8445";
        return "8443";
    }

    private String getEndpointForMessageType(ConsensusMessage.MessageType type) {
        switch (type) {
            case PROPOSE: return "/api/consensus/propose";
            case PREPARE: return "/api/consensus/prepare";
            case COMMIT: return "/api/consensus/commit";
            default: throw new IllegalArgumentException("Unknown message type");
        }
    }

    private RestTemplate createRestTemplate() {
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

    // Inner class for PROPOSE payload
    public static class ProposePayload {
        private ConsensusMessage message;
        private Block block;

        public ProposePayload() {}

        public ProposePayload(ConsensusMessage message, Block block) {
            this.message = message;
            this.block = block;
        }

        public ConsensusMessage getMessage() { return message; }
        public void setMessage(ConsensusMessage message) { this.message = message; }

        public Block getBlock() { return block; }
        public void setBlock(Block block) { this.block = block; }
    }
}