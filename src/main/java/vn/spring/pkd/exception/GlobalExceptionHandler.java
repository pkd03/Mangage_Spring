package vn.spring.pkd.exception;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.spring.pkd.dto.request.ApiResponse;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ApiResponse<?>> exceptionBusiness(BusinessException ex){
        log.warn("Error business exception with message={}", ex.getMessage(), ex);

        ApiResponse<?> response = ApiResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getStatus().value())
                .body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> exception(Exception ex){
        log.warn("Error exception message={}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value() )
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(response);
    }
}
