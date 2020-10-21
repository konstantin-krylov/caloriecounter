package com.epam.caloriecounter.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FillDatabaseResponse {
    private Integer recordsAdded;
    private Long requestDuration;
    private Integer missedCalls;
}
