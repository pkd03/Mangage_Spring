package vn.spring.pkd.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.spring.pkd.entity.User;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtService {

    @NonFinal //Field này không được tự động thêm final.
    @Value("${jwt.signerKey}")
    protected String SECRET;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

//    Thời gian hết hạn của Access Token
    protected long expireExpiraToken = 604800000; // 7 NGÀY

//    Thời gian hết hạn của Refresh Token
    //    @Value("${spring.token.expireRefreshToken}")
    protected long expireRefreshToken = 5500000000L; // 60 NGÀY

    Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String generateToken(String username, User user){
        Map<String, Object> claims = new HashMap<>();


//        nhồi nhét thông tin vào biến claims để trả cho FE
        claims.put("id", user.getId());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("dateOfBirth", user.getDateOfBirth());
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("duy", "Duy");

//        lấy Quyền (Role) cao nhất của người dùng bỏ vào token
//        List<Role> list = user.getRoleList();
//        if (user != null && user.getRoleList().size() >= 0) {
//            for (Role r : list) {
//                if (r.getNameRole().equals("ADMIN")) {
//                    claims.put("role", "ADMIN");
//                    break;
//                }
//                if (r.getNameRole().equals("STAFF")) {
//                    claims.put("role", "STAFF");
//                    break;
//                }
//                if (r.getNameRole().equals("CUSTOMER")) {
//                    claims.put("role", "CUSTOMER");
//                    break;
//                }
//            }
//        }
        return createToken(claims, username);

    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSignKey()).parseClaimsJws(token).getBody();
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims) // Thiết lập Payload (dữ liệu)
                .setSubject(username) // Thiết lập Subject (chủ thể - thường là username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Thời gian tạo
                .setExpiration(new Date(System.currentTimeMillis() + expireExpiraToken)) // JWT hết hạn sau 30p
                .signWith(SignatureAlgorithm.HS256, getSignKey())  //Ký bằng thuật toán HS256 và Secret Key
                .compact();
    }

    // Lấy select key mã hóa base64
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // kiểm tra token đã hết hạn hay chưa
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Trichs xuất thông số thời gian hết hạn từ JWT
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
