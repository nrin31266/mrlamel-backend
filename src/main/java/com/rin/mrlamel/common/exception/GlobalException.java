package com.rin.mrlamel.common.exception;

import com.rin.mrlamel.common.dto.response.ApiRes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiRes> exception(Exception ex) {
        ErrorCode errorCode = ErrorCode.DEFAULT_ERROR;
        ex.printStackTrace();
        return ResponseEntity.status(errorCode.getStatusCode()).body(ApiRes.builder()
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiRes> handleAppException(AppException e) {
        return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(ApiRes.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiRes> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatusCode()).body(ApiRes.builder()
                .code(errorCode.getCode())
                .message(Objects.requireNonNull(e.getFieldError()).getDefaultMessage() != null ? e.getFieldError().getDefaultMessage() : "Invalid input")
                .build());
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    ResponseEntity<ApiRes> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        return ResponseEntity.status(errorCode.getStatusCode()).body(ApiRes.builder()
                .code(errorCode.getCode())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    ResponseEntity<ApiRes> handleMissingRequestCookieException(MissingRequestCookieException e) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatusCode()).body(ApiRes.builder()
                .code(errorCode.getCode())
                .message("Missing required cookie: " + e.getCookieName())
                .build());
    }
}
