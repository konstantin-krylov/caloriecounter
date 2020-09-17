package com.epam.caloriecounter.exception;

abstract class AbstractAlreadyExistException extends RuntimeException {

    AbstractAlreadyExistException(String message) {
        super(message);
    }
}
