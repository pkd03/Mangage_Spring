package vn.spring.pkd.service.auth;

import ch.qos.logback.classic.Logger;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.spring.pkd.dto.request.auth.AuthenticationRequest;
import vn.spring.pkd.dto.request.auth.IntrospectRequest;
import vn.spring.pkd.dto.response.AuthenticationResponse;
import vn.spring.pkd.dto.response.IntrospectResponse;
import vn.spring.pkd.entity.User;
import vn.spring.pkd.entity.UserRole;
import vn.spring.pkd.repository.UserRepository;
import vn.spring.pkd.repository.UserRoleRepository;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceIml implements AuthenticationService{


    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal //Field này không được tự động thêm final.
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

//    @NonFinal
//    @Value("${jwt.refreshable-duration}")
//    protected long REFRESHABLE_DURATION;


    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }

//    Verify password, gọi generateToken
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("user k tồn tại"));

//        passwordEncoder.matches(passwordUserNhap, passwordDaHashTrongDB);
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated) throw new RuntimeException("Unauthenticated");

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }


    //    Tạo JWT HS512
    @Override
    public String generateToken(User user) {
        //        JWT = HEADER.PAYLOAD.SIGNATURE

                //        HEADER
//        HS512 = HMAC + SHA-512 — thuật toán ký đối xứng (dùng 1 secret key để ký và verify).
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

                //        PAYLOAD
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) // của user nào
                .issuer("pkd") // phát hành bởi ai
                .issueTime(new Date()) // time token đc tao
                //time token hết hạn= time hiện tại(vừa tao) + time duy trì token
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString()) //Mỗi token tạo ra một UUID khác nhau.
                .claim("userId", "Custom")
                .build();

//khi có jwtClaimsSet thì chuyển thành payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

//        HEADER +  PAYLOAD +  SECRET KEY  ->  HS512  ->SIGNATURE
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            Logger log = null;
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }

    }

//    UserRoleRepository userRoleRepository;
////    Nhúng role + permission vào JWT
//    private String buildScope(User user) {
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        var listRoleByUser = userRoleRepository
//                .findByUserId(user.getId());
//        UserRole userRole = new UserRole();
//        if (!CollectionUtils.isEmpty(listRoleByUser.get))
//            user.getRoles().forEach(role -> {
//                stringJoiner.add("ROLE_" + role.getName());
//            });
//
//        return stringJoiner.toString();
//    }
}
