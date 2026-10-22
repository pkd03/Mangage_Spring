package vn.tnteco.study.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true, nullable = false)
    String username;

    @Column(nullable = false)
    String password;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "user_type", nullable = false)
//    UserType userType; // CUSTOMER hoặc STAFF
}
