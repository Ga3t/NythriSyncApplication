package com.caliq.FoodSecretApiConnection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class FoodSecretApiConnectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodSecretApiConnectionApplication.class, args);
	}

}
