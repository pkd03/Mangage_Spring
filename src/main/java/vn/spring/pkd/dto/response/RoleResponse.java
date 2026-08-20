package vn.spring.pkd.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String id;
    String roleCode;
    String roleName;
    LocalDateTime createAt;
    LocalDateTime updateAt;
    LocalDateTime deleteAt;
//    Không cần trả deletedAt.
}
