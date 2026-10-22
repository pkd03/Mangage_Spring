package vn.tnteco.study.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt lỗi nghiệp vụ do chính chúng ta ném ra (AppException)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(response);
    }

    // 2. Bắt lỗi validate DTO từ @Valid (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        // Lấy chi tiết từng field bị lỗi và thông điệp tương ứng
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT.getCode())
                .message(ErrorCode.INVALID_INPUT.getMessage())
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getStatusCode())
                .body(response);
    }

    // 1. Bắt lỗi nhập sai đường dẫn URL / tài nguyên không tồn tại (404 Not Found)
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception exception) {
        ErrorCode errorCode = ErrorCode.PATH_NOT_FOUND;

        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode()) // 404
                .body(response);
    }

    // 2. Bắt lỗi không đủ quyền hạn (403 Forbidden) do @PreAuthorize ném ra
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode()) // 403
                .body(response);
    }


    // 3. Lưới bọc cuối cùng: Bắt tất cả các lỗi chưa lường trước (NullPointer, lỗi kết nối DB,...)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnwantedException(Exception exception) {
        // In log ra console server để lập trình viên xem và debug
        exception.printStackTrace();

        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(response);
    }
}
