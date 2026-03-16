package com.easybuy.UserService.Exception;

import lombok.Data;

@Data
public class UserServiceException extends RuntimeException
{
    private final String errorCode;
    public UserServiceException(String errorCode,String message)
    {
        super(message);
        this.errorCode=errorCode;
    }

}
