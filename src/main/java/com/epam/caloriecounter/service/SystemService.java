package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FillDatabaseRequest;
import com.epam.caloriecounter.dto.FillDatabaseResponse;
import com.epam.caloriecounter.repository.FoodRepository;
import com.epam.caloriecounter.utils.RandomValueGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemService {

    private final FoodService foodService;
    private final FoodRepository foodRepository;
    private final RandomValueGenerator randomValueGenerator;

    public FillDatabaseResponse fill(FillDatabaseRequest request) {
        int missed = 0;
        int count = 0;
        long startTime = System.nanoTime();
        List<Long> allFdcIds = foodRepository.getAllIds();

        while (count != request.getNumberOfRecords()) {
            Long randomNum = randomValueGenerator.generate();
            try {
                if (!allFdcIds.contains(randomNum)) {
                    foodService.saveFood(String.valueOf(randomNum));
                }
            } catch (Exception e) {
                missed++;
                continue;
            }
            count++;
        }
        return new FillDatabaseResponse()
                .setRecordsAdded(count)
                .setMissedCalls(missed)
                .setRequestDuration(TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime));
    }
}
