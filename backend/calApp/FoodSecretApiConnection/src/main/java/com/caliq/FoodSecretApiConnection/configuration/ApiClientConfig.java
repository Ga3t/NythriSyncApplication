package com.caliq.FoodSecretApiConnection.configuration;


import com.caliq.FoodSecretApiConnection.components.LoadAccessTokenForApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiClientConfig {

    @Bean
    public RestClient oauthRestClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public RestClient apiRestClient(RestClient.Builder builder, LoadAccessTokenForApi tokenService) {
        return builder
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(tokenService.getAccessToken());
                    return execution.execute(request, body);
                })
                .build();
    }
}
