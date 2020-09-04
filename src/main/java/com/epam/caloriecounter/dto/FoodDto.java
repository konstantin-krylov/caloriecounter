package com.epam.caloriecounter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodDto {

    private Long foodId;
    private String foodTitle;
    private String foodDescription;
    private String foodIngredients;
    private String foodTypeTitle;
    private Set<FoodNutrientDto> foodNutrients;

}
