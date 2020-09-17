package com.epam.caloriecounter.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class FoodNutrientDto {
    private String nutrientName;
    private String unitName;
    private Float amount;
}
