package vn.tnteco.study.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Cho phép dùng @PreAuthorize trên Controller / Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    JwtAuthenticationFilter jwtAuthenticationFilter;
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    //cmt do đã cấu hình riêng
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập công khai các API authentication và xem hàng client
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/client/**").permitAll()

                        // 3. Mở quyền cho /error và DispatcherType để không bị chặn 403 khi forward
                        .requestMatchers("/error").permitAll()
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR,
                                jakarta.servlet.DispatcherType.FORWARD).permitAll()

                        // Tất cả các request còn lại phải được xác thực
                        .anyRequest().authenticated()
                )
                // Đăng ký JwtFilter vào chuỗi Filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }




    /**
     * Định nghĩa cây phân cấp Role:
     * ADMIN có toàn quyền của STAFF; STAFF có toàn quyền của CLIENT.
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_CLIENT");
    }
    /**
     * Đăng ký RoleHierarchy vào bộ xử lý biểu thức SpEL của @PreAuthorize (Method Security)
     */
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }
}
