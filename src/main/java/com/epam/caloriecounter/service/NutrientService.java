package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodNutrientResponse;
import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.entity.FoodNutrient;
import com.epam.caloriecounter.entity.NutrientType;
import com.epam.caloriecounter.properties.NutrientTypeProperties;
import com.epam.caloriecounter.repository.NutrientTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutrientService {

    private final NutrientTypeRepository nutrientTypeRepository;
    private final NutrientTypeProperties nutrientTypeProperties;

    public HashSet<FoodNutrient> getFoodNutrients(FoodItemResponse foodItemResponse, Food food) {

        HashSet<FoodNutrient> foodNutrients = new HashSet<>();
        for (FoodNutrientResponse response : foodItemResponse.getFoodNutrients()) {
            if (!isNutrientUseful(response.getNutrient().getNumber())) {
                continue;
            }

            FoodNutrient foodNutrient = new FoodNutrient()
                    .setAmount(response.getAmount())
                    .setFood(food)
                    .setNutrientType(checkOnExistingNutrientType(response));

            foodNutrients.add(foodNutrient);
        }
        return foodNutrients;
    }

    private boolean isNutrientUseful(String nutrientNumber) {
        return nutrientTypeProperties.getNutrients()
                .keySet()
                .stream()
                .anyMatch(nutNum -> nutNum.equals(nutrientNumber));
    }

    private NutrientType checkOnExistingNutrientType(FoodNutrientResponse response) {
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

    private String getNurientByNutrientNumber(String nutrientNumber) {
        return nutrientTypeProperties.getNutrients().entrySet()
                .stream()
                .filter(x -> x.getKey().equals(nutrientNumber))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow();
    }
}
