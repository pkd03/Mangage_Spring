package vn.spring.pkd.service.auth;

import ch.qos.logback.classic.Logger;
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
import vn.spring.pkd.service.jwt.JwtService;

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

        var token = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
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
