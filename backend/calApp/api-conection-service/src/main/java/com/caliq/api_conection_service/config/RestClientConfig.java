package com.caliq.api_conection_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        String nameApp = "FoodFeedApp";
        String appVersion = "0.1";
        String contactEmailApp = "foodfeed84@gmail.com";

        String userAgent = String.format("%s/%s (%s)", nameApp, appVersion, contactEmailApp);

        return RestClient.builder()
                .defaultHeader("User-Agent", userAgent)
                .build();
    }
}
