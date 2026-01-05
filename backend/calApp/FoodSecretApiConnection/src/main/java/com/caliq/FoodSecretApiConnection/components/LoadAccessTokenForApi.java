package com.caliq.FoodSecretApiConnection.components;

import com.caliq.FoodSecretApiConnection.records.OAuthTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@Component
public class LoadAccessTokenForApi implements ApplicationRunner {

    private final RestClient oauthClient;

    @Value("${fatsecret.oauth.token-url}")
    private String tokenUrl;

    @Value("${fatsecret.oauth.client-id}")
    private String clientId;

    @Value("${fatsecret.oauth.client-secret}")
    private String clientSecret;

    @Value("${fatsecret.oauth.scope:basic}")
    private String scope;

    private static final String GRANT_TYPE = "client_credentials";

    private volatile String accessToken;
    private volatile Instant refreshAt = Instant.EPOCH;

    public LoadAccessTokenForApi(
            @Qualifier("oauthRestClient") RestClient oauthClient
    ) {
        this.oauthClient = oauthClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting FatSecret OAuth token initialization");
        refreshToken();
    }

    @Scheduled(fixedDelay = 60_000)
    public void scheduledRefresh() {
        ensureValidToken();
    }

    public String getAccessToken() {
        ensureValidToken();
        return accessToken;
    }

    private synchronized void ensureValidToken() {
        if (Instant.now().isAfter(refreshAt)) {
            refreshToken();
        }
    }

    private void refreshToken() {
        log.info("Refreshing FatSecret OAuth token");
        log.info("tokenUrl={}", tokenUrl);
        log.info("clientId present={}", clientId != null && !clientId.isBlank());
        log.info("clientSecret present={}", clientSecret != null && !clientSecret.isBlank());
        log.info("scope={}", scope);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", GRANT_TYPE);
        form.add("scope", scope);

        OAuthTokenResponse res;

        try {
            res = oauthClient
                    .post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .headers(h -> h.setBasicAuth(clientId, clientSecret, StandardCharsets.UTF_8))
                    .body(form)
                    .retrieve()
                    .body(OAuthTokenResponse.class);
        } catch (Exception e) {
            log.error("OAuth token request failed", e);
            throw e;
        }

        log.info("OAuth response is null={}", res == null);

        if (res == null || res.accessToken() == null) {
            log.error(
                    "Failed to obtain OAuth token. clientId present={}, clientSecret present={}",
                    clientId != null && !clientId.isBlank(),
                    clientSecret != null && !clientSecret.isBlank()
            );
            throw new IllegalStateException("Failed to obtain access token");
        }

        this.accessToken = res.accessToken();
        this.refreshAt = Instant.now().plusSeconds(
                Math.max(0, res.expiresIn() - 60)
        );

        log.info(
                "FatSecret OAuth token refreshed successfully, expires at {}",
                refreshAt
        );
    }
}
