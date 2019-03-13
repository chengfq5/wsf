package com.wsf.core.exceptions;

public class WsfRpcException extends RuntimeException {

    public WsfRpcException(String message) {
        super(message);
    }

    public WsfRpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
