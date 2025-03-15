package vn.xuanhung.ELearning_Service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.xuanhung.ELearning_Service.common.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(exception = AppException.class)
    public ResponseEntity<ApiResponse<AppException>> handlerAppException(AppException appException){
        ErrorCode errorCode = appException.getErrorCode();
        ApiResponse<AppException> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus())
                .body(apiResponse);
    }
}
