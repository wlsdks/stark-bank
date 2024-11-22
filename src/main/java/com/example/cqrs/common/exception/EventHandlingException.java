package com.example.cqrs.common.exception;

import lombok.Getter;

@Getter
public class EventHandlingException extends RuntimeException {

    public EventHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

}