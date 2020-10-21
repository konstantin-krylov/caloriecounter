package com.epam.caloriecounter.exception;

public class FoodException extends AbstractException {

    private static final String MESSAGE = "Food with fdc_id %s already exists";

    public FoodException(Long fdcId) {
        super(String.format(MESSAGE, fdcId));
    }
}
