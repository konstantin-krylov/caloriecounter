package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodDto;
import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodNutrientDto;
import com.epam.caloriecounter.dto.FoodNutrientResponse;
import com.epam.caloriecounter.dto.FoodSearchRequestDto;
import com.epam.caloriecounter.dto.FoodSearchResultResponseDto;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodDataCentralService {

    private final UsdaApiGateway usdaApiGateway;
    private final FoodMapper foodMapper;
    private final NutrientTypeProperties nutrientTypeProperties;

    private final FoodRepository foodRepository;
    private final NutrientTypeRepository nutrientTypeRepository;
    private final FoodTypeRepository foodTypeRepository;

    public FoodItemResponse getFood(String fdcId, String format, List<Integer> nutrients) {
        return usdaApiGateway.getFood(fdcId, format, nutrients);
    }

    public FoodSearchResultResponseDto search(FoodSearchRequestDto request) {
        return usdaApiGateway.search(request);
    }

    // TODO Code refactoring
    // TODO remove redundant nutrient types
    public FoodDto saveFood(String fdcId) {
        FoodItemResponse foodItemResponse = usdaApiGateway.getFood(fdcId, "full", Collections.emptyList());

        Food food = new Food()
                .setFoodTitle(foodItemResponse.getDescription())
                .setFoodIngredients(foodItemResponse.getIngredients());

        FoodType foodType = foodTypeRepository.findByFoodTypeTitle(foodItemResponse.getDataType());
        if (Objects.isNull(foodType)) {
            food.setFoodType(new FoodType()
                    .setFoodTypeTitle(foodItemResponse.getDataType()));
        } else {
            food.setFoodType(foodType);
        }

        HashSet<FoodNutrient> foodNutrients = new HashSet<>();
        for (FoodNutrientResponse response : foodItemResponse.getFoodNutrients()) {

            FoodNutrient foodNutrient = new FoodNutrient()
                    .setAmount(response.getAmount())
                    .setFood(food);

            String nutrientNumber = response.getNutrient().getNumber();
            NutrientType nutrientType = nutrientTypeRepository.findByNutrientNumber(nutrientNumber);
            if (isNutrientUseful(nutrientNumber)) {
                if (Objects.isNull(nutrientType)) {
                    foodNutrient.setNutrientType(new NutrientType()
                            .setNutrientName(getNurientByNutrientNumber(nutrientNumber))
                            .setNutrientNumber(response.getNutrient().getNumber())
                            .setUnitName(response.getNutrient().getUnitName().toLowerCase()));
                } else {
                    foodNutrient.setNutrientType(nutrientType);
                }
                foodNutrients.add(foodNutrient);
            }
        }
        food.setFoodNutrients(foodNutrients);
        Food savedFood = foodRepository.save(food);

        return constructFoodDto(savedFood);
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
