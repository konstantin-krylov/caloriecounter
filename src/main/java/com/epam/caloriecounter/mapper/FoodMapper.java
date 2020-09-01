package com.epam.caloriecounter.mapper;

import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.entity.Food;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FoodMapper {

    @Mapping(target = "foodTitle", source = "description")
    @Mapping(target = "foodType.foodType", source = "dataType")
    @Mapping(target = "foodIngredients", source = "ingredients")
    Food toFoodEntity(FoodItemResponse foodItemResponse);

}
