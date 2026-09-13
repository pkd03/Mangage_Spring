package vn.spring.pkd.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessException extends RuntimeException{
    private String message;
    private HttpStatus status;
    private  Integer code;

}
