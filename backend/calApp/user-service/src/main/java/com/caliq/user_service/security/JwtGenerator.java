package com.caliq.user_service.security;
import com.caliq.user_service.models.enums.Roles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import utils.PemUtils;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
@Component
public class JwtGenerator {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private static final long expTime = 30*60*1000;
    public JwtGenerator() {}
    @PostConstruct
    public void initKeys() throws Exception {
        try (InputStream privateKeyStream = getClass().getClassLoader().getResourceAsStream("keys/key.priv");
             InputStream publicKeyStream = getClass().getClassLoader().getResourceAsStream("keys/key.pub")) {
            if (privateKeyStream == null || publicKeyStream == null) {
                throw new FileNotFoundException("PEM key files not found in classpath");
            }
            this.privateKey = PemUtils.loadPrivateKey(privateKeyStream);
            this.publicKey = PemUtils.loadPublicKey(publicKeyStream);
        }
    }
    public String generateToken(Authentication authentication, Long userId, Roles role){
        Date currDate = new Date();
        Date expDate = new Date(currDate.getTime() + expTime);
        String username = authentication.getName();
        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .claim("role", role)
                .setIssuedAt(currDate)
                .setExpiration(expDate)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    public PublicKey getPublicKey(){
        return publicKey;
    }
}