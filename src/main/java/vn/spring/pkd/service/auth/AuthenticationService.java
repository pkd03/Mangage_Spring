package vn.spring.pkd.service.auth;

import org.springframework.http.ResponseEntity;
import vn.spring.pkd.dto.request.auth.AuthenticationRequest;
import vn.spring.pkd.dto.request.auth.JwtRequest;
import vn.spring.pkd.dto.response.AuthenticationResponse;
import vn.spring.pkd.entity.User;

public interface AuthenticationService {
    void logout(JwtRequest jwtRequest);
    ResponseEntity<?> refresh(JwtRequest jwtRequest);

    AuthenticationResponse sosicalogin(String authencode);

    AuthenticationResponse authenticate(AuthenticationRequest request);

//    String generateToken(User user);
}
