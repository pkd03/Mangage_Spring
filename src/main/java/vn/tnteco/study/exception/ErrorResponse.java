package vn.tnteco.study.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Trường nào null (như errors) thì không hiển thị trong JSON
public class ErrorResponse {
    int code;                    // Mã lỗi nội bộ (lấy từ ErrorCode)
    String message;              // Thông điệp lỗi
    Map<String, String> errors;  // Chi tiết lỗi theo từng trường (dùng khi validate Bean Validation)
    LocalDateTime timestamp;     // Thời điểm xảy ra lỗi
}
