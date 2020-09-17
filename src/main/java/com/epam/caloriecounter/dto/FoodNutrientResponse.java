package com.epam.caloriecounter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodNutrientResponse {
    private String id;
    private String type;
    private Float amount;

    /*
    USDA API can return nutrient object...
     */
    private Nutrient nutrient;
    /*
    ...or fields
     */
    private String number;
    private String name;
    private String unitName;


    @Setter
    @Getter
    public static class Nutrient {

        private Integer id;
        private String number;
        private String name;
        private Integer rank;
        private String unitName;

    }
}
