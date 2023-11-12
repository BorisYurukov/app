package com.community.weare.Exceptions;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super((message));
    }
    public DuplicateEntityException(String type, String attribute, String name) {
        super(String.format("%s with %s %s already exists!", type, attribute,name));
    }
}
