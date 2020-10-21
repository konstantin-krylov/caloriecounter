package com.epam.caloriecounter.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Setter
@Getter
@ApiModel
public class FoodSearchRequestDto {

    @NotEmpty
    @ApiModelProperty(value = "Search terms to use in the search", example = "Cheese", required = true)
    private String query;

    @ApiModelProperty(value = "The type of the food data",
            example = "[\"Branded\",\"Foundation\",\"Survey (FNDDS)\",\"SR Legacy\"]")
    private List<String> dataType;

    @Max(200)
    @ApiModelProperty(value = "Optional. Maximum number of results to return for the current page. Default is 50", example = "25")
    private Integer pageSize;

    @ApiModelProperty(value = "Optional. Page number to retrieve", example = "1")
    private Integer pageNumber;

    @ApiModelProperty(value = "Optional. Specify one of the possible values to sort by that field",
            example = "dataType.keyword")
    private String sortBy;

    @ApiModelProperty(value = "Optional. The sort direction for the results", example = "asc")
    private String sortOrder;

    @ApiModelProperty(value = "Optional. Filter results based on the brand owner of the food. Only applies to Branded Foods",
            example = "Kar Nut Products Company")
    private String brandOwner;

}
