package vn.spring.pkd.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.spring.pkd.repository.UserRepository;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    UserRepository userRepository;

    // Dich vu nap thong tin nguoi dung cho Spring Security
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay nguoi dung: " + username));
    }

    // Authentication provider su dung password encoder
//    AuthenticationProvider là nơi thực hiện logic xác thực thật sự:
//    lấy UserDetails qua userDetailsService(), rồi so sánh mật khẩu người dùng nhập
//    với mật khẩu đã mã hoá trong DB bằng passwordEncoder().
//    DaoAuthenticationProvider là implementation có sẵn của Spring,
//    chuyên xác thực bằng username/password lấy từ database
//            (DAO = Data Access Object).
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
//        Trong các phiên bản Spring Security mới (từ khoảng Spring Security 6.3 trở lên,
//        và bắt buộc từ Spring Security 7 / Spring Boot 4),
//        Spring đã bỏ (remove) phương thức setUserDetailsService()
//        khỏi DaoAuthenticationProvider. Thay vì dùng constructor không tham số
//        rồi gọi setUserDetailsService(), giờ phải dùng constructor
//        có tham số UserDetailsService ngay từ đầu,
//        và các lệnh gọi setUserDetailsService() đã bị loại bỏ vì đây là phương thức deprecated.
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    // AuthenticationManager duoc expose thanh bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // BCrypt de ma hoa mat khau an toan
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
