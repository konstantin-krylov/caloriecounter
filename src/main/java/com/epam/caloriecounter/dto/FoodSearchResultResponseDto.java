package com.epam.caloriecounter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodSearchResultResponseDto {

    private Long totalHits;
    private Long currentPage;
    private Long totalPages;

    private FoodSearchCriteria foodSearchCriteria;

    @JsonProperty("foods")
    private List<SearchResultFood> searchResultFoods;

    @Data
    public static class SearchResultFood {
        private Long fdcId;
        private String dataType;
        private String description;
        private String brandOwner;
        private String ingredients;
    }

    @Data
    public static class FoodSearchCriteria {
        private String generalSearchInput;
        private Boolean requireAllWords;
    }
}



