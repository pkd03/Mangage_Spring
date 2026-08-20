package vn.spring.pkd.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignRoleRequest {
    String roleId;
//    Service sẽ xử lý:
//    userId = 1
//    roleId = 2
//            ↓
//    check User
//       ↓
//    check Role
//       ↓
//    check user_roles
//       ↓
//    insert
}
