package com.epam.caloriecounter.controller;

import com.epam.caloriecounter.dto.FoodDto;
import com.epam.caloriecounter.service.FoodService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class FoodController {

    private final FoodService foodService;

    @PostMapping(path = "get-and-save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FoodDto> getFood(@RequestParam @ApiParam(example = "477320") String fdcId) {
        return ResponseEntity.ok(foodService.saveFood(fdcId));
    }
}
