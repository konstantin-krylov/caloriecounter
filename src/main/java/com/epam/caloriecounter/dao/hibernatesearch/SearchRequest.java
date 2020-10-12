package com.epam.caloriecounter.dao.hibernatesearch;

import com.epam.caloriecounter.dto.CustomSort;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"searchBar", "pageSize", "pageNumber",
        "offset", "paged", "unpaged", "filters"})
public class SearchRequest extends PageRequest {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private transient Map<String, List<Object>> filters;
    private String searchBar;
    private CustomSort sorting;

    public SearchRequest(@JsonProperty("pageNumber") int pageNumber,
                         @JsonProperty("pageSize") int pageSize,
                         @JsonProperty("filters") Map<String, List<Object>> filters,
                         @JsonProperty("searchBar") String searchBar) {
        super(pageNumber, initPageSize(pageSize), Sort.unsorted());
        this.filters = filters;
        this.searchBar = searchBar;
    }

    private static int initPageSize(int pageSize) {
        return pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}