package com.caliq.api_gateway.components;
//
//import com.caliq.core.events.GetPublicKeyRequestEvent;
//import com.caliq.core.events.GetPublicKeyResponseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//@Component
//public class LoadPublicKey {
//
//    @Autowired
//    private final KafkaTemplate<String, GetPublicKeyRequestEvent> kafkaTemplate;
//
//    public LoadPublicKey(KafkaTemplate<String, GetPublicKeyRequestEvent> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void requestPublicKey(String requestId) {
//        GetPublicKeyRequestEvent event = new GetPublicKeyRequestEvent(requestId);
//        kafkaTemplate.send("get-public-key-request", event);
//    }
//
//    private String lastReceivedKey;
//
//    @KafkaListener(topics = "get-public-key-response", groupId = "api-service")
//    public void consume(GetPublicKeyResponseEvent event) {
//       System.out.println("Received key from Kafka");
//
//        this.lastReceivedKey = event.getPublicKey();
//
//        try {
//            File file = new File("key.pub");
//            try (FileWriter writer = new FileWriter(file)) {
//                writer.write(lastReceivedKey);
//            }
//            System.out.println("Public key saved to key.pub");
//        } catch (IOException e) {
//            System.err.println("Failed to save public key to file: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public String getLastReceivedKey() {
//        return lastReceivedKey;
//    }
//}
