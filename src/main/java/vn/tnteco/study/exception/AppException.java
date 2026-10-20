package vn.tnteco.study.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException{
    private final ErrorCode errorCode;
    public AppException(ErrorCode errorCode) {
        // Truyền message của ErrorCode vào super để log hiển thị rõ ràng
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
