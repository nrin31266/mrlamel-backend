package com.rin.mrlamel.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DEFAULT_ERROR(500, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(401, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "Forbidden access", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
    BAD_REQUEST(400, "Bad request", HttpStatus.BAD_REQUEST),
    CONFLICT(409, "Conflict occurred", HttpStatus.CONFLICT),
    ;


    final int code;
    final String message;
    private final HttpStatusCode statusCode;

    public String getFormattedMessage(Object... args) {
        return String.format(this.message, args);
    }
}
