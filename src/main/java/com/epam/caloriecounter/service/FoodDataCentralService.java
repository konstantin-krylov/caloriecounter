package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodSearchRequestDto;
import com.epam.caloriecounter.dto.FoodSearchResultResponseDto;
import com.epam.caloriecounter.gateway.UsdaApiGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodDataCentralService {

    private final UsdaApiGateway usdaApiGateway;

    public FoodSearchResultResponseDto search(FoodSearchRequestDto request) {
        return usdaApiGateway.search(request);
    }

    public FoodItemResponse getFood(String fdcId, String format, List<Integer> nutrients) {
        return usdaApiGateway.getFood(fdcId, format, nutrients);
    }
}
