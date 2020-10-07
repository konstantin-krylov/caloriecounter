package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dao.hibernatesearch.SearchRequest;
import com.epam.caloriecounter.dao.impl.FoodSearchDao;
import com.epam.caloriecounter.dto.FoodDto;
import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodNutrientDto;
import com.epam.caloriecounter.dto.FoodNutrientResponse;
import com.epam.caloriecounter.dto.ShortFoodDto;
import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.entity.FoodNutrient;
import com.epam.caloriecounter.entity.FoodType;
import com.epam.caloriecounter.entity.NutrientType;
import com.epam.caloriecounter.exception.FoodAlreadyExistException;
import com.epam.caloriecounter.gateway.UsdaApiGateway;
import com.epam.caloriecounter.mapper.FoodMapper;
import com.epam.caloriecounter.properties.NutrientTypeProperties;
import com.epam.caloriecounter.repository.FoodRepository;
import com.epam.caloriecounter.repository.FoodTypeRepository;
import com.epam.caloriecounter.repository.NutrientTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    private final FoodSearchDao foodSearchDao;

    public FoodDto saveFood(String fdcId) {
        FoodItemResponse foodItemResponse = usdaApiGateway.getFood(fdcId, FOOD_DATA_FORMAT, Collections.emptyList());
        checkOnFoodDublicate(foodItemResponse.getFdcId());

        Food food = new Food();
        food.setFoodTitle(foodItemResponse.getDescription());
        food.setFdcId(foodItemResponse.getFdcId());
        food.setFoodIngredients(foodItemResponse.getIngredients());
        food.setFoodType(checkOnExistingFoodType(foodItemResponse.getDataType()));
        food.setFoodNutrients(getFoodNutrients(foodItemResponse, food));

        Food savedFood = foodRepository.save(food);

        return constructFoodDto(savedFood);
    }

    private void checkOnFoodDublicate(Long fdcId) {
        if (!Objects.isNull(foodRepository.findByFdcId(fdcId))) {
            throw new FoodAlreadyExistException(fdcId);
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

    private HashSet<FoodNutrient> getFoodNutrients(FoodItemResponse foodItemResponse, Food food) {

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

    private FoodDto constructFoodDto(Food savedFood) {
        Set<FoodNutrientDto> foodNutrientDtos = new HashSet<>();
        for (FoodNutrient nutrient : savedFood.getFoodNutrients()) {
            FoodNutrientDto foodNutrientDto = foodMapper.toFoodNutrientDto(nutrient);
            foodNutrientDtos.add(foodNutrientDto);
        }
        return foodMapper.toFoodDto(savedFood)
                .setFoodNutrients(foodNutrientDtos);
    }

    public Page<ShortFoodDto> searchFood(SearchRequest request) {
        List<Food> foods = foodSearchDao.searchFoodNameByFuzzyQuery(request.getSearchBar());
        List<ShortFoodDto> foodDtos = new ArrayList<>();
        for (Food food : foods) {
            foodDtos.add(foodMapper.toShortFoodDto(food));
        }

        return getPageFromList(foodDtos, request, foodDtos.size());
    }

    private Page<ShortFoodDto> getPageFromList(List<ShortFoodDto> executionList, Pageable pageable, long maxSize) {
        int toIndex = (pageable.getOffset() + pageable.getPageSize() > executionList.size()) ?
                executionList.size() : (int) (pageable.getOffset() + pageable.getPageSize());
        int fromIndex = toIndex > pageable.getOffset() ? (int) pageable.getOffset() : toIndex;
        return new PageImpl<>(
                executionList.subList(fromIndex, toIndex),
                pageable, maxSize);
    }
}
