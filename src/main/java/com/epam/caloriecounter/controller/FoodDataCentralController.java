package com.epam.caloriecounter.controller;

import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodSearchRequestDto;
import com.epam.caloriecounter.dto.FoodSearchResultResponseDto;
import com.epam.caloriecounter.service.FoodDataCentralService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class FoodDataCentralController {
    private final FoodDataCentralService foodDataCentralService;

    @PostMapping(path = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FoodSearchResultResponseDto> searchFood(@RequestBody FoodSearchRequestDto request) {
        return ResponseEntity.ok(foodDataCentralService.search(request));
    }

    @GetMapping(path = "get/{fdcId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FoodItemResponse> getFood(@PathVariable @ApiParam(example = "477320") String fdcId,
                                                    @RequestParam(required = false)
                                                    @ApiParam(allowableValues = "abridged,full", defaultValue = "abridged") String format,
                                                    @RequestParam(required = false) List<Integer> nutrients) {
        return ResponseEntity.ok(foodDataCentralService.getFood(fdcId, format, nutrients));
    }
}
