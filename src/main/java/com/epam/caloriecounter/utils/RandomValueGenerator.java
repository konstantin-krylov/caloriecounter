package com.epam.caloriecounter.utils;

import org.springframework.stereotype.Component;

@Component
public class RandomValueGenerator {

    public static final int MAX_FDC_ID = 999999;
    public static final int MIN_FDC_ID = 100000;

    public long generate() {
        return (long) ((Math.random() * (MAX_FDC_ID - MIN_FDC_ID)) + MIN_FDC_ID);
    }
}
