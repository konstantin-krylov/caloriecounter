package com.epam.caloriecounter.exception;

abstract class AbstractException extends RuntimeException {

    AbstractException(String message) {
        super(message);
    }
}
