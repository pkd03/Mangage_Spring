package vn.tnteco.study.service.auth;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.tnteco.study.dto.request.user.LoginRequest;
import vn.tnteco.study.dto.response.auth.AuthenticationResponse;
import vn.tnteco.study.exception.AppException;
import vn.tnteco.study.exception.ErrorCode;
import vn.tnteco.study.model.User;
import vn.tnteco.study.repository.user.UserRepository;
import vn.tnteco.study.repository.user.UserRoleRepository;
import vn.tnteco.study.security.JwtTokenProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    UserRoleRepository userRoleRepository;
    PasswordEncoder passwordEncoder;
    JwtTokenProvider jwtTokenProvider;

    public AuthenticationResponse login(LoginRequest request) {
        // 1. Tìm User theo username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        // 2. So khớp password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 3. TRỌNG TÂM: Lấy danh sách Roles qua User ID từ Bảng Trung Gian UserRole
        List<String> roles = userRoleRepository.findRoleIdsByUserId(user.getId());

        // 4. Sinh JWT Token chứa thông tin User và danh sách Roles
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId(), roles);

        return AuthenticationResponse.builder()
                .token(token)
                .username(user.getUsername())
                .roles(roles)
                .build();
    }
}
