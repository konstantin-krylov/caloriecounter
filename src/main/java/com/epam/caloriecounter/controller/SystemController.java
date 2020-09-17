package com.epam.caloriecounter.controller;

import com.epam.caloriecounter.dto.FillDatabaseRequest;
import com.epam.caloriecounter.dto.FillDatabaseResponse;
import com.epam.caloriecounter.service.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
public class SystemController {

    private final SystemService systemService;

    @PostMapping("/fill")
    public FillDatabaseResponse fill(FillDatabaseRequest request) {
        return systemService.fill(request);
    }
}
