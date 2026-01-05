package com.caliq.user_service.services;

//import org.springframework.core.io.Resource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//@Service
//public class KeyResponder {
//
//    @Value("classpath:key.pub")
//    private Resource publicKeyResource;
//
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    @KafkaListener(topics = "key-request-topic", groupId = "key-service-group")
//    public void onKeyRequest(String message) throws IOException {
//        String publicKey = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//        kafkaTemplate.send("key-response-topic", publicKey);
//    }
//}