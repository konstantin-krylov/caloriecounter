package com.epam.caloriecounter.dto;

import com.epam.caloriecounter.entity.FoodNutrient;
import com.epam.caloriecounter.entity.LabelNutrients;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodItemResponse {

    private Long fdcId;
    private String brandOwner;
    private String dataSource;
    private String dataType;
    private String description;
    private String foodClass;
    private String gtinUpc;
    private String householdServingFullText;
    private String ingredients;
    private Float servingSize;
    private String servingSizeUnit;
    private String brandedFoodCategory;
    private LabelNutrients labelNutrients;
    private List<FoodNutrient> foodNutrients;

}
