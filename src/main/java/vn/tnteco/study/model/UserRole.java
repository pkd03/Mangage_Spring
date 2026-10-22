package vn.tnteco.study.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "role_id"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(name = "role_id", nullable = false)
    String roleId; // Có thể lưu tên role (ví dụ: "ADMIN", "CUSTOMER")
}
