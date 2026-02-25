package com.jupyter.order_service.Exception;

public class MissingAuthenticationTokenException extends RuntimeException {

    public MissingAuthenticationTokenException(String message) {
        super(message);
    }
}
