package com.dev.driver;

public class NoDriversAvailableException extends RuntimeException {

    public NoDriversAvailableException(final String message) {
        super(message);
    }
}
