package com.epam.caloriecounter.dto;

import lombok.Data;

@Data
public class FoodNutrientDto {
    private String nutrientName;
    private String unitName;
    private Float amount;
}
