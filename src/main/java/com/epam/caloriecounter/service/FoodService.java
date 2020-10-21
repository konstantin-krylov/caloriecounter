package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodDto;
import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodNutrientDto;
import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.entity.FoodNutrient;
import com.epam.caloriecounter.entity.FoodType;
import com.epam.caloriecounter.exception.FoodException;
import com.epam.caloriecounter.gateway.UsdaApiGateway;
import com.epam.caloriecounter.mapper.FoodMapper;
import com.epam.caloriecounter.repository.FoodRepository;
import com.epam.caloriecounter.repository.FoodTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {

    public static final String FOOD_DATA_FORMAT = "full";

    private final UsdaApiGateway usdaApiGateway;
    private final FoodMapper foodMapper;
    private final FoodRepository foodRepository;
    private final FoodTypeRepository foodTypeRepository;
    private final NutrientService nutrientService;

    public FoodDto saveFood(String fdcId) {
        FoodItemResponse foodItemResponse = usdaApiGateway.getFood(fdcId, FOOD_DATA_FORMAT, Collections.emptyList());
        checkOnFoodDublicate(foodItemResponse.getFdcId());

        Food food = new Food();
        food.setFoodTitle(foodItemResponse.getDescription());
        food.setFdcId(foodItemResponse.getFdcId());
        food.setFoodIngredients(foodItemResponse.getIngredients());
        food.setFoodType(checkOnExistingFoodType(foodItemResponse.getDataType()));
        food.setFoodNutrients(nutrientService.getFoodNutrients(foodItemResponse, food));

        Food savedFood = foodRepository.save(food);

        return constructFoodDto(savedFood);
    }

    private void checkOnFoodDublicate(Long fdcId) {
        if (!Objects.isNull(foodRepository.findByFdcId(fdcId))) {
            throw new FoodException(fdcId);
        }
    }

    private FoodType checkOnExistingFoodType(String dataType) {
        FoodType foodType = foodTypeRepository.findByFoodTypeTitle(dataType);
        if (Objects.isNull(foodType)) {
            return new FoodType()
                    .setFoodTypeTitle(dataType);
        }
        return foodType;
    }

    private FoodDto constructFoodDto(Food savedFood) {
        Set<FoodNutrientDto> foodNutrientDtos = new HashSet<>();
        for (FoodNutrient nutrient : savedFood.getFoodNutrients()) {
            FoodNutrientDto foodNutrientDto = foodMapper.toFoodNutrientDto(nutrient);
            foodNutrientDtos.add(foodNutrientDto);
        }
        return foodMapper.toFoodDto(savedFood)
                .setFoodNutrients(foodNutrientDtos);
    }
}
