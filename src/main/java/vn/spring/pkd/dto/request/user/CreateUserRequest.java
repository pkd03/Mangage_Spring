package vn.spring.pkd.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    String firstName;

    String lastName;

//    @Size(min = 3,message = "USERNAME_INVALID")
    String username;

//    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    Date dateOfBirth;

}
