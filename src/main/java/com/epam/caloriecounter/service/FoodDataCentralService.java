package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodNutrientResponse;
import com.epam.caloriecounter.dto.FoodSearchRequestDto;
import com.epam.caloriecounter.dto.FoodSearchResultResponseDto;
import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.entity.FoodNutrient;
import com.epam.caloriecounter.entity.NutrientType;
import com.epam.caloriecounter.gateway.UsdaApiGateway;
import com.epam.caloriecounter.mapper.FoodMapper;
import com.epam.caloriecounter.properties.NutrientTypeProperties;
import com.epam.caloriecounter.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodDataCentralService {

    private final UsdaApiGateway usdaApiGateway;
    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;
    private final NutrientTypeProperties nutrientTypeProperties;

    public FoodSearchResultResponseDto search(FoodSearchRequestDto request) {
        return usdaApiGateway.search(request);
    }

    public FoodItemResponse getFood(String fdcId, String format, List<Integer> nutrients) {
        FoodItemResponse foodItemResponse = usdaApiGateway.getFood(fdcId, format, nutrients);

        HashSet<FoodNutrient> foodNutrients = new HashSet<>();
        FoodNutrient foodNutrient;
        Food food = foodMapper.toFoodEntity(foodItemResponse);
        for (FoodNutrientResponse response : foodItemResponse.getFoodNutrients()) {

            if (foodItemResponse.getDataType().equals("Branded")) {
                foodNutrient = new FoodNutrient()
                        .setNutrientType(new NutrientType()
                                .setNutrientName(mapNutrientBrandedType(response, foodItemResponse))
                                .setUnitName(response.getNutrient().getUnitName()))
                        .setAmount(response.getAmount())
                        .setFood(food);

            } else {
                foodNutrient = new FoodNutrient()
                        .setNutrientType(new NutrientType()
                                .setNutrientName(mapNutrient(response, foodItemResponse))
                                .setUnitName(response.getUnitName()))
                        .setAmount(response.getAmount())
                        .setFood(food);

            }

            foodNutrients.add(foodNutrient);
        }
        food.setFoodNutrients(foodNutrients);

        foodRepository.save(food);
        return foodItemResponse;
    }

    private String mapNutrient(FoodNutrientResponse response, FoodItemResponse foodItemResponse) {
        return nutrientTypeProperties.getNutrients().entrySet()
                .stream()
                .filter(x -> x.getKey().equals(response.getNumber()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private String mapNutrientBrandedType(FoodNutrientResponse response, FoodItemResponse foodItemResponse) {
        return nutrientTypeProperties.getNutrients().entrySet()
                .stream()
                .filter(x -> x.getKey().equals(response.getNutrient().getNumber()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }
}
