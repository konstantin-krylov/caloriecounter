package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dao.hibernatesearch.SearchRequest;
import com.epam.caloriecounter.dao.impl.FoodSearchDao;
import com.epam.caloriecounter.dto.ShortFoodDto;
import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.mapper.FoodMapper;
import com.epam.caloriecounter.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final FoodSearchDao foodSearchDao;
    private final FoodMapper foodMapper;

    public Page<ShortFoodDto> searchFood(SearchRequest request) {
        List<Food> foods = foodSearchDao.searchFoodNameByFuzzyQuery(request.getSearchBar());

        List<ShortFoodDto> foodDtos = new ArrayList<>();
        for (Food food : foods) {
            foodDtos.add(foodMapper.toShortFoodDto(food));
        }

        return PageUtils.getPageFromList(foodDtos, request, foodDtos.size());
    }
}
