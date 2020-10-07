package com.epam.caloriecounter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortFoodDto {

    private Long foodId;
    private String foodTitle;
    private String foodDescription;
    private String foodIngredients;
    private String foodTypeTitle;

}
