package com.epam.caloriecounter.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomSort {
    private String direction;
    private List<String> properties;
}
