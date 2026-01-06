package com.caliq.api_gateway.service;
import com.caliq.api_gateway.utils.PemUtils;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PublicKey;
@Component
public class JwtValidator {
    private PublicKey publicKey;
    public JwtValidator() {}
    public JwtValidator(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
    @PostConstruct
    public void initKeys() throws Exception {
        try (InputStream publicKeyStream = getClass().getClassLoader().getResourceAsStream("key.pub")) {
            if (publicKeyStream == null) {
                throw new FileNotFoundException("PEM key files not found in classpath");
            }
            this.publicKey = PemUtils.loadPublicKey(publicKeyStream);
        }
    }
    public boolean validateAccessToken(String token){
        try{
            Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
            System.out.println("True");
            return true;
        }catch (Exception e){
            System.out.println("False");
            return false;
        }
    }
    public Long getUserIdFromJwt(String token){
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody().get("id", Long.class);
    }
}