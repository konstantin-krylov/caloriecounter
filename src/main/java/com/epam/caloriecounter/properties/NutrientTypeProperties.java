package com.epam.caloriecounter.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


@Setter
@Getter
@ConfigurationProperties("usda.properties")
public class NutrientTypeProperties {
    private Map<String, String> nutrients;
}
