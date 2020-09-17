package com.epam.caloriecounter.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomValueGenerator {

    public int generate() {
        return ThreadLocalRandom.current().nextInt(100000, 999999 + 1);
    }
}
