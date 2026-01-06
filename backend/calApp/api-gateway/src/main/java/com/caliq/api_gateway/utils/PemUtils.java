package com.caliq.api_gateway.utils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
@Component
public class PemUtils {
    public static PublicKey loadPublicKey(String resourcePath) throws Exception {
        try (InputStream inputStream = new ClassPathResource(resourcePath).getInputStream()) {
            return loadPublicKey(inputStream);
        }
    }
    public static PublicKey loadPublicKey(InputStream inputStream) throws Exception {
        String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}