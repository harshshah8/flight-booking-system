package com.fbs;

import lombok.Builder;

import java.io.Serial;

@Builder
public class GenericException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -676390742147669618L;
    private Integer errorCode;
    private String message;
    private String displayMessage;

    public GenericException(Integer errorCode, String message, String displayMessage) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.displayMessage = displayMessage;
    }

    public GenericException(Integer errorCode, String message, String displayMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.displayMessage = displayMessage;
    }

    public static GenericException buildException(Integer errorCode) {
        return builder().errorCode(errorCode).build();
    }

    public static GenericException buildException(Integer errorCode, String message) {
        return builder().errorCode(errorCode).message(message).build();
    }

    public static GenericException buildException(Integer errorCode, String message, String displayMessage) {
        return builder().errorCode(errorCode).message(message).displayMessage(displayMessage).build();
    }
}