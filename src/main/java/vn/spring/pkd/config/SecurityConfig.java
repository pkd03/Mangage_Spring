package vn.spring.pkd.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.spring.pkd.service.jwt.JwtFilter;


import javax.crypto.spec.SecretKeySpec;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    @Value("${jwt.signerKey}")
    private String signerKey;


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình phân quyền cho endpoint
        httpSecurity.authorizeHttpRequests(config -> config
                .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_ENDPOINT).permitAll()
                .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_ENDPOINT).permitAll()
                .requestMatchers(HttpMethod.PUT, Endpoints.PUBLIC_PUT_ENDPOINT).permitAll()
                .requestMatchers(HttpMethod.DELETE, Endpoints.PUBLIC_DELETE_ENDPOINT).permitAll()

                // Các endpoint ADMIN cần quyền "ADMIN"
                .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_ENDPOINT).hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_ENDPOINT).hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, Endpoints.ADMIN_ENDPOINT).hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, Endpoints.ADMIN_ENDPOINT).hasAuthority("ADMIN")

                // Những request không khớp cấu hình trên thì bắt buộc phải đăng nhập (có token)
                .anyRequest().authenticated()
        );
        httpSecurity.addFilterBefore(jwtFilter,  UsernamePasswordAuthenticationFilter.class);
         // Kích hoạt OAuth2 Resource Server để Spring Security biết đọc JWT
//        httpSecurity.oauth2ResourceServer(oauth2 ->
//                oauth2.jwt(jwtConfigurer ->
//                        jwtConfigurer.decoder(jwtDecoder())
//                                     // Converter giúp chuyển đổi claims 'scope' thành Authority
//                                     .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                )
//        );

// Security khi dùng sẽ auto csrf bảo vệ toàn bộ endpoint từ đó luôn bị 403, có thể tạm tắt đi

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // Bỏ chữ "SCOPE_" mặc định của Spring
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope"); // Đọc quyền từ field "scope" trong JWT
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }

}

