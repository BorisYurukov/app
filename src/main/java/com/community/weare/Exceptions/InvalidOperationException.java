package com.community.weare.Exceptions;

public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String s) {
        super(s);
    }

    public InvalidOperationException() {
        super(String.format("Invalid operation!"));
    }
}
