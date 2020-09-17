package com.epam.caloriecounter.exception;

public class FoodAlreadyExistException extends AbstractAlreadyExistException {

    private static final String MESSAGE = "Food with fdc_id %s already exists";

    public FoodAlreadyExistException(Long fdcId) {
        super(String.format(MESSAGE, fdcId));
    }
}
