package com.epam.caloriecounter.mapper;

import com.epam.caloriecounter.dto.FoodDto;
import com.epam.caloriecounter.dto.FoodNutrientDto;
import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.entity.FoodNutrient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FoodMapper {

    @Mapping(target = "foodTypeTitle", source = "foodItemResponse.foodType.foodTypeTitle")
    FoodDto toFoodDto(Food foodItemResponse);

    @Mapping(target = "nutrientName", source = "nutrient.nutrientType.nutrientName")
    @Mapping(target = "unitName", source = "nutrient.nutrientType.unitName")
    FoodNutrientDto toFoodNutrientDto(FoodNutrient nutrient);
}
