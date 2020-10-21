package com.epam.caloriecounter.exception;

public class IndexingProcessException extends AbstractException {

    private static final String MESSAGE = "Indexing process already called";

    public IndexingProcessException() {
        super(MESSAGE);
    }
}
