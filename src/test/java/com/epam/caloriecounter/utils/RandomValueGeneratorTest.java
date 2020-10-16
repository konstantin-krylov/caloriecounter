package com.epam.caloriecounter.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RandomValueGeneratorTest {

    @Autowired
    private RandomValueGenerator randomValueGenerator;
    public static final int MAX_FDC_ID = 999999;
    public static final int MIN_FDC_ID = 100000;

    @Test
    void generate_shouldGenerateValueInTheGivenRange() {
        long generated = randomValueGenerator.generate();
        assertTrue(MIN_FDC_ID <= generated && generated <= MAX_FDC_ID);
    }

}