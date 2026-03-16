package com.easybuy.UserService.Exception;

public class AddressException extends RuntimeException {

    private String errorCode;

    public AddressException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}