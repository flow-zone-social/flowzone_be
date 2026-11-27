package com.fsocial.shared.exception;

import com.fsocial.shared.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Void>> handlingUnknownException(Exception exception) {
        log.error("Unhandled exception", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                .statusCode(StatusCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message("A system error occurred. Please try again later.")
                .dateTime(LocalDateTime.now())
                .data(null)
                .build());
    }

    @ExceptionHandler(value = NonTransientAiException.class)
    ResponseEntity<ApiResponse<Void>> handlingAiServiceException(NonTransientAiException exception) {
        log.error("Lỗi gọi AI provider (model không hợp lệ, không tồn tại hoặc đã deprecated): {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiResponse.<Void>builder()
                .statusCode(StatusCode.AI_SERVICE_FAILED.getCode())
                .message("AI service is currently unavailable. Please try again later.")
                .dateTime(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message("You do not have permission to perform this action")
                .dateTime(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
        var code = exception.getErrorCode();
        return ResponseEntity.status(code.getHttpStatusCode())
                .body(ApiResponse.<Void>builder()
                        .statusCode(code.getCode())
                        .message(exception.getMessage())
                        .dateTime(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    ResponseEntity<ApiResponse<Void>> handlingMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                .statusCode(StatusCode.FILE_TOO_LARGE.getCode())
                .message("Uploaded file exceeds the allowed system limit.")
                .dateTime(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> handlingNotFoundException(NoResourceFoundException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                .statusCode(StatusCode.ENDPOINT_NOT_FOUND.getCode())
                .message("Endpoint not found: " + exception.getResourcePath())
                .dateTime(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        try {
            StatusCode errorCode = StatusCode.valueOf(enumKey);
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .statusCode(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .dateTime(LocalDateTime.now())
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .statusCode(StatusCode.INVALID_PARAMETER.getCode())
                    .message(enumKey)
                    .dateTime(LocalDateTime.now())
                    .build());
        }
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    ResponseEntity<ApiResponse<Void>> handlingMethodIllegalStateException(MissingServletRequestParameterException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                .statusCode(StatusCode.MISSING_PARAMETER.getCode())
                .message("Missing required parameter: " + exception.getParameterName())
                .dateTime(LocalDateTime.now())
                .build());
    }

}
