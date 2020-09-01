package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodItemResponse;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodDataCentralService {

    private final UsdaApiGateway usdaApiGateway;
    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;
    private final NutrientTypeProperties nutrientTypeProperties;
    private final NutrientTypeRepository nutrientTypeRepository;
    private final FoodTypeRepository foodTypeRepository;

    public FoodSearchResultResponseDto search(FoodSearchRequestDto request) {
        return usdaApiGateway.search(request);
    }

    //TODO remove redundant nutrient types
    //TODO fix not all saving nutrient type when format = abridged
    //TODO add endpoint to save in db
    // TODO Code refactoring
    public FoodItemResponse getFood(String fdcId, String format, List<Integer> nutrients) {
        FoodItemResponse foodItemResponse = usdaApiGateway.getFood(fdcId, format, nutrients);

        HashSet<FoodNutrient> foodNutrients = new HashSet<>();

        Food food = foodMapper.toFoodEntity(foodItemResponse);
        FoodType foodType = foodTypeRepository.findByFoodType(foodItemResponse.getDataType());
        if (Objects.isNull(foodType)) {
            food.setFoodType(new FoodType()
                    .setFoodType(foodItemResponse.getDataType()));
        } else {
            food.setFoodType(foodType);
        }

        for (FoodNutrientResponse response : foodItemResponse.getFoodNutrients()) {

            FoodNutrient foodNutrient = new FoodNutrient()
                    .setAmount(response.getAmount())
                    .setFood(food);

            if (format.equals("full")) {
                String nutrientName = response.getNutrient().getName();
                NutrientType nutrientType = nutrientTypeRepository.findByNutrientName(nutrientName);

                if (Objects.isNull(nutrientType)) {
                    foodNutrient.setNutrientType(new NutrientType()
                            .setNutrientName(nutrientName)
                            .setUnitName(response.getNutrient().getUnitName().toLowerCase()));
                } else {
                    foodNutrient.setNutrientType(nutrientType);
                }
            } else {
                String nutrientName = response.getName();
                NutrientType nutrientType = nutrientTypeRepository.findByNutrientName(nutrientName);

                if (Objects.isNull(nutrientType)) {
                    foodNutrient.setNutrientType(new NutrientType()
                            .setNutrientName(nutrientName)
                            .setUnitName(response.getUnitName().toLowerCase()));
                } else {
                    foodNutrient.setNutrientType(nutrientType);
                }
            }

            foodNutrients.add(foodNutrient);
        }
        food.setFoodNutrients(foodNutrients);

        Food save = foodRepository.save(food);
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
