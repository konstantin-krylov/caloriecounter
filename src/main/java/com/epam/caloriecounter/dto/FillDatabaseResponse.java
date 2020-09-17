package com.epam.caloriecounter.dto;

import lombok.Data;

@Data
public class FillDatabaseResponse {
    private Integer recordsAdded;
    private Long requestDuration;
    private Integer missedCalls;
}
