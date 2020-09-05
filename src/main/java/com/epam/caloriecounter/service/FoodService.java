package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodDto;
import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodNutrientDto;
import com.epam.caloriecounter.dto.FoodNutrientResponse;
import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.entity.FoodNutrient;
import com.epam.caloriecounter.entity.FoodType;
import com.epam.caloriecounter.entity.NutrientType;
import com.epam.caloriecounter.gateway.UsdaApiGateway;
import com.epam.caloriecounter.mapper.FoodMapper;
import com.epam.caloriecounter.properties.NutrientTypeProperties;
import com.epam.caloriecounter.repository.FoodRepository;
import com.epam.caloriecounter.repository.FoodTypeRepository;
import com.epam.caloriecounter.repository.NutrientTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {

    public static final String FOOD_DATA_FORMAT = "full";
    private final UsdaApiGateway usdaApiGateway;
    private final FoodMapper foodMapper;
    private final NutrientTypeProperties nutrientTypeProperties;

    private final FoodRepository foodRepository;
    private final NutrientTypeRepository nutrientTypeRepository;
    private final FoodTypeRepository foodTypeRepository;

    public FoodDto saveFood(String fdcId) {
        FoodItemResponse foodItemResponse = usdaApiGateway.getFood(fdcId, FOOD_DATA_FORMAT, Collections.emptyList());

        Food food = new Food();
        food.setFoodTitle(foodItemResponse.getDescription());
        food.setFoodIngredients(foodItemResponse.getIngredients());
        food.setFoodType(checkOnExistingAndReturnFoodType(foodItemResponse.getDataType()));
        food.setFoodNutrients(getFoodNutrients(foodItemResponse, food));

        Food savedFood = foodRepository.save(food);

        return constructFoodDto(savedFood);
    }

    private HashSet<FoodNutrient> getFoodNutrients(FoodItemResponse foodItemResponse, Food food) {

        HashSet<FoodNutrient> foodNutrients = new HashSet<>();
        for (FoodNutrientResponse response : foodItemResponse.getFoodNutrients()) {
            if (!isNutrientUseful(response.getNutrient().getNumber())) {
                continue;
            }

            FoodNutrient foodNutrient = new FoodNutrient()
                    .setAmount(response.getAmount())
                    .setFood(food)
                    .setNutrientType(checkOnExistingAndReturnNutrientType(response));

            foodNutrients.add(foodNutrient);
        }
        return foodNutrients;
    }

    private NutrientType checkOnExistingAndReturnNutrientType(FoodNutrientResponse response) {
        String nutrientNumber = response.getNutrient().getNumber();
        NutrientType nutrientType = nutrientTypeRepository.findByNutrientNumber(nutrientNumber);

        if (Objects.isNull(nutrientType)) {
            return new NutrientType()
                    .setNutrientName(getNurientByNutrientNumber(nutrientNumber))
                    .setNutrientNumber(nutrientNumber)
                    .setUnitName(response.getNutrient().getUnitName().toLowerCase());
        } else {
            return nutrientType;
        }
    }

    private FoodType checkOnExistingAndReturnFoodType(String dataType) {
        FoodType foodType = foodTypeRepository.findByFoodTypeTitle(dataType);
        if (Objects.isNull(foodType)) {
            return new FoodType()
                    .setFoodTypeTitle(dataType);
        }
        return foodType;
    }

    private boolean isNutrientUseful(String nutrientNumber) {
        return nutrientTypeProperties.getNutrients()
                .keySet()
                .stream()
                .anyMatch(nutNum -> nutNum.equals(nutrientNumber));
    }

    private String getNurientByNutrientNumber(String nutrientNumber) {
        return nutrientTypeProperties.getNutrients().entrySet()
                .stream()
                .filter(x -> x.getKey().equals(nutrientNumber))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow();
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
