package vn.xuanhung.ELearning_Service.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException{
    ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.message);
        this.errorCode = errorCode;
    }
}
