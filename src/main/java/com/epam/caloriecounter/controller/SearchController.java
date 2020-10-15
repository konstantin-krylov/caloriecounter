package com.epam.caloriecounter.controller;

import com.epam.caloriecounter.dao.hibernatesearch.SearchRequest;
import com.epam.caloriecounter.dao.impl.FoodSearchDao;
import com.epam.caloriecounter.dto.ShortFoodDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class SearchController {

    private final FoodSearchDao foodSearchDao;

    @PostMapping(path = "search-food", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ShortFoodDto>> searchFood(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(foodSearchDao.search(request));
    }
}
