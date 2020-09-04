package com.epam.caloriecounter;

import com.epam.caloriecounter.properties.NutrientTypeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({NutrientTypeProperties.class,})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
