package com.epam.caloriecounter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties("usda.properties")
public class NutrientTypeProperties {
    private Map<String, String> nutrients;
}
