package com.example.cqrs.exception;

public class ConcurrencyException extends RuntimeException {

    public ConcurrencyException(String message) {
        super(message);
    }

}
