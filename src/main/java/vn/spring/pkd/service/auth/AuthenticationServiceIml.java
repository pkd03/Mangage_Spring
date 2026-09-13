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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vn.spring.pkd.dto.request.auth.AuthenticationRequest;
import vn.spring.pkd.dto.request.auth.IntrospectRequest;
import vn.spring.pkd.dto.request.auth.JwtRequest;
import vn.spring.pkd.dto.response.AuthenticationResponse;
import vn.spring.pkd.dto.response.IntrospectResponse;
import vn.spring.pkd.entity.User;
import vn.spring.pkd.entity.UserRole;
import vn.spring.pkd.exception.BusinessException;
import vn.spring.pkd.repository.UserRepository;
import vn.spring.pkd.repository.UserRoleRepository;
import vn.spring.pkd.service.jwt.JwtService;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationServiceIml implements AuthenticationService{


    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserRoleRepository userRoleRepository;
    JwtService jwtService;
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

    @Override
    public void logout(JwtRequest jwtRequest) {

    }

    @Override
    public ResponseEntity<?> refresh(JwtRequest jwtRequest) {
        return null;
    }

    @Override
    public AuthenticationResponse sosicalogin(String authencode) {
        return null;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if(user == null){
            throw new BusinessException("Nguoi dung khong ton tai", HttpStatus.NOT_FOUND, 404);
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BusinessException("Maajt khau khong dung", HttpStatus.BAD_REQUEST, 400);
        }
        String token = jwtService.generateToken(user.getUsername(), user);

        if(!StringUtils.hasText(token)){
            throw new BusinessException("Dang nhap khong thanh cong", HttpStatus.BAD_GATEWAY, 502);
        }

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();

    }


}
