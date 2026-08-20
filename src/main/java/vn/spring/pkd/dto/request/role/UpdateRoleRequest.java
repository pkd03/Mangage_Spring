package vn.spring.pkd.dto.request.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoleRequest {
//    Theo yêu cầu chỉ sửa name
    String roleName;

}
