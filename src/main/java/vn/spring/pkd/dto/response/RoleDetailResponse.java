package vn.spring.pkd.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDetailResponse {
    Long id;

    String code;

    String name;

    List<UserResponse> users;
}
