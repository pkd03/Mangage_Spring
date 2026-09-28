package vn.spring.pkd.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.signerKey}")
    private String signerKey;

    // Thoi gian song cua token (mac dinh 24h, tinh bang ms)
    @Value("${jwt.valid-duration}")
    private long validDuration;

    // Sinh token cho mot nguoi dung da xac thuc
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Sinh token voi cac claim tuy chinh them vao
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validDuration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    //    Chuyển chuỗi Base64 trong config thành đối tượng SecretKey dùng cho thuật toán HMAC-SHA (HS256).
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(signerKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }




    // Lay username (subject) tu token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Lay mot claim cu the thong qua ham trich xuat
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //    giải mã và xác minh chữ ký token bằng cùng khoá bí mật.
//    Nếu chữ ký sai hoặc token hết hạn, hàm này tự ném exception (JwtException, ExpiredJwtException).
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    //Kiem tra token hop le: chu ky dung va chua het han
    //true khi  username trong token khớp và chưa hết hạn
//    fail khi 1 trong 2 sai
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
