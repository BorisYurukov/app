package com.community.weare.Exceptions;

import java.util.function.Supplier;

public class EntityNotFoundException extends RuntimeException implements Supplier<EntityNotFoundException> {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String type, String attribute, String name) {
        super(String.format("%s with %s %s doesn't exists!", type, attribute,name));
    }


    @Override
    public EntityNotFoundException get() {
        return new EntityNotFoundException();
    }
}
