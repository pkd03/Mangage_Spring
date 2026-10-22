package vn.tnteco.study.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Value("${jwt.valid-duration}")
    private long validDuration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(signerKey.getBytes(StandardCharsets.UTF_8));
    }

    // 1. Sinh Token: đưa username và danh sách Roles vào Claims
    public String generateToken(String username, String userId, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validDuration);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("roles", roles) // Lưu thẳng danh sách role vào payload
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    // 2. Trích xuất Claims từ Token
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    // 3. Kiểm tra tính hợp lệ của Token
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
