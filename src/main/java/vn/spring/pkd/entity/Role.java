package vn.spring.pkd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "role")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "role_code", length = 100, nullable = false, unique = true)
    private String roleCode;

    @Column(name = "role_name", length = 256)
    private String roleName;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @Column(name = "is_delete")
    private boolean isDelete;

    //    @PrePersist: Tự động chạy method gán createAt = LocalDateTime.now()
//    và isDelete = false ngay trước khi câu lệnh INSERT SQL được thực thi.
    @PrePersist
    public void prePersist() {
        if (this.createAt == null) {
            this.createAt = LocalDateTime.now();
        }
        this.isDelete = false;
    }

    //    @PreUpdate: Tự động chạy method gán updateAt = LocalDateTime.now()
//    ngay trước khi UPDATE SQL được thực thi.
    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
