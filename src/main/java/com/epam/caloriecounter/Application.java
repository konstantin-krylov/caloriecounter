package com.epam.caloriecounter;

import com.epam.caloriecounter.properties.NutrientTypeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties({NutrientTypeProperties.class,})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
