package vn.spring.pkd.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse <T>{
    int code ;
    String message;
    T result;

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
