package com.caliq.api_gateway.configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity https){
        https
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("auth/**").permitAll()
                        .anyExchange().permitAll());
        return https.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.addAllowedOriginPattern("https://*.polandcentral.azurecontainerapps.io");
        configuration.addAllowedOriginPattern("http://*.polandcentral.azurecontainerapps.io");
        
       
        configuration.addAllowedOriginPattern("https://nythrisyncfront.*.polandcentral.azurecontainerapps.io");
        configuration.addAllowedOriginPattern("https://nythrisync.*.polandcentral.azurecontainerapps.io");
        configuration.addAllowedOrigin("https://nythrisyncfront.mangoforest-8f324fa1.polandcentral.azurecontainerapps.io");
        configuration.addAllowedOrigin("https://nythrisync.yellowpebble-0fd0af74.polandcentral.azurecontainerapps.io");
        
     
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedOriginPattern("http://localhost:*");
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "X-Refresh-Token", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Refresh-Token", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}