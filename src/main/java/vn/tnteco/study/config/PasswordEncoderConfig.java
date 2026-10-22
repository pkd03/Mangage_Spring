package vn.tnteco.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//Vì sao nên tách riêng ra file này?
//Tránh lỗi vòng lặp phụ thuộc (Circular Dependency): Khi Spring khởi động,
//SecurityConfig cần inject JwtAuthenticationFilter.
//Nếu trong tương lai filter hoặc AuthenticationService cần PasswordEncoder,
//mà PasswordEncoder lại nằm bên trong SecurityConfig, Spring sẽ bị kẹt trong vòng lặp khởi tạo
//(SecurityConfig -> Service -> PasswordEncoder -> SecurityConfig).
// Việc tách ra file riêng giúp Bean PasswordEncoder được khởi tạo độc lập ngay từ đầu.
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
