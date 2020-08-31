package com.epam.caloriecounter.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodNutrient {
    private String id;
    private String type;
    private Nutrient nutrient;

    private String number;
    private String name;
    private Float amount;
    private String unitName;

    @Data
    public static class Nutrient {

        private Integer id;
        private String number;
        private String name;
        private Integer rank;
        private String unitName;

    }
}
