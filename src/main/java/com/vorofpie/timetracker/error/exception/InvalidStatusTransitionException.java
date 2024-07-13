package com.vorofpie.timetracker.error.exception;

public class InvalidStatusTransitionException extends RuntimeException{
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
