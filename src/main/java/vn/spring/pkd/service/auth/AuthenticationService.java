package vn.spring.pkd.service.auth;

import vn.spring.pkd.dto.request.auth.AuthenticationRequest;
import vn.spring.pkd.dto.response.AuthenticationResponse;
import vn.spring.pkd.entity.User;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    String generateToken(User user);
}
