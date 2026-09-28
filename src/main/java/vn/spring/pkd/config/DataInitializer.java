package vn.spring.pkd.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.spring.pkd.entity.User;
import vn.spring.pkd.repository.UserRepository;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataInitializer implements CommandLineRunner {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Value("${admin.default.username}")
    String defaultAdminUsername;

    @Value("${admin.default.password}")
    String defaultAdminPassword;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findByUsername(defaultAdminUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(defaultAdminUsername);
            admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
            admin.setRoles(Set.of("ROLE_ADMIN"));
            userRepository.save(admin);
            System.out.println(">>> Đã tạo tài khoản admin mặc định: " + defaultAdminUsername);
        } else {
            System.out.println(">>> Tài khoản admin đã tồn tại, bỏ qua khởi tạo.");
        }
    }
}
