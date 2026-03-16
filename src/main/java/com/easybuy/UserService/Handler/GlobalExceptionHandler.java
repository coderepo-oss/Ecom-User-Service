package com.easybuy.UserService.Handler;

import com.easybuy.UserService.Exception.AddressException;
import com.easybuy.UserService.Exception.UserServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse response = new ErrorResponse("Unauthorized", "Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserServiceException ex) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

        HttpStatus status = switch (ex.getErrorCode()) {
            case "EMAIL_ALREADY_EXISTS", "PHONENUMBER_ALREADY_EXIST", "EMAIL_NOT_FOUND", "INVALID_PASSWORD"
                    -> HttpStatus.BAD_REQUEST;
            case "INVALID_CREDENTIALS", "INVALID_TOKEN", "PROVIDER_REQUIRED"
                    -> HttpStatus.UNAUTHORIZED;
            case "USER_NOT_FOUND"
                    -> HttpStatus.NOT_FOUND;
            default
                    -> HttpStatus.BAD_GATEWAY;
        };
        log.error("UserServiceException: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AddressException.class)
    public ResponseEntity<ErrorResponse> handleAddressException(AddressException ex) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

        HttpStatus status = switch (ex.getErrorCode()) {
            case "ADDRESS_LIMIT_EXCEEDED", "INVALID_ADDRESS_DATA"
                    -> HttpStatus.BAD_REQUEST;
            case "FORBIDDEN"
                    -> HttpStatus.FORBIDDEN;
            case "ADDRESS_NOT_FOUND"
                    -> HttpStatus.NOT_FOUND;
            default
                    -> HttpStatus.BAD_GATEWAY;
        };
        log.error("AddressServiceException: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);
        return new ResponseEntity<>(error, status);
    }
}