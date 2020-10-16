package com.epam.caloriecounter.dao.impl;

import com.epam.caloriecounter.dao.hibernatesearch.SearchRequest;
import com.epam.caloriecounter.dto.ShortFoodDto;
import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.mapper.FoodMapper;
import com.epam.caloriecounter.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FoodSearchDao {

    private static final String SEARCH_BAR_REQUEST = "searchBar";
    private static final String INGREDIENTS_FILTER = "ingredients";

    private static final String FOOD_TITLE_FIELD_NAME = "foodTitle";
    public static final String FOOD_INGREDIENTS_FIELD_NAME = "foodIngredients";

    private static final float HIGH_PRIORITY = 5F;
    private static final float MEDIUM_PRIORITY = 3F;

    @PersistenceContext
    private EntityManager entityManager;

    private final FoodMapper foodMapper;

    @Transactional(readOnly = true)
    public Page<ShortFoodDto> search(SearchRequest searchRequest) {

        Map<String, List<Object>> filterParams = addSearchBarQueryToFilters(searchRequest);

        FullTextQuery fullTextQuery = createFullTextQueryFromFilters(
                filterParams,
                getFullTextEntityManager(),
                getQueryBuilder()
        );

        @SuppressWarnings("unchecked")
        List<Food> resultList = (List<Food>) fullTextQuery.getResultList();

        List<ShortFoodDto> foodDtos = new ArrayList<>();
        for (Food food : resultList) {
            foodDtos.add(foodMapper.toShortFoodDto(food));
        }

        return PageUtils.getPageFromList(foodDtos, searchRequest, foodDtos.size());
    }

    private Map<String, List<Object>> addSearchBarQueryToFilters(SearchRequest searchRequest) {

        String searchBarQuery = searchRequest.getSearchBar();

        if (StringUtils.isNotBlank(searchBarQuery)) {

            Map<String, List<Object>> filters = searchRequest.getFilters();
            if (filters == null) {
                filters = new HashMap<>();
            }
            filters.put(SEARCH_BAR_REQUEST, Collections.singletonList(searchBarQuery));
            return filters;

        } else {
            return searchRequest.getFilters();
        }
    }

    private FullTextQuery createFullTextQueryFromFilters(Map<String, List<Object>> filterParams,
                                                         FullTextEntityManager fullTextEntityManager,
                                                         QueryBuilder queryBuilder) {
        if (filterParams == null || filterParams.isEmpty()) {
            return fullTextEntityManager.createFullTextQuery(queryBuilder.all().createQuery(), Food.class);
        }

        BooleanJunction<?> query = queryBuilder.bool();

        for (Map.Entry<String, List<Object>> entry : filterParams.entrySet()) {

            if (StringUtils.isNotBlank(entry.getKey()) && entry.getValue() != null) {
                Query customQuery = processCustomFilter(entry, queryBuilder);
                if (customQuery != null) {
                    query.must(customQuery);
                } else {
                    query.should(queryBuilder.all().createQuery());
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return fullTextEntityManager.createFullTextQuery(query.createQuery(), Food.class);
    }

    private Query processCustomFilter(Map.Entry<String, List<Object>> entry, QueryBuilder queryBuilder) {
        if (entry.getKey().equals(SEARCH_BAR_REQUEST)) {
            String searchString = (String) entry.getValue().get(0);

            String[] splittedSearchString = parseSearchBarString(searchString);

            BooleanJunction<?> query = queryBuilder.bool();

            for (String singleWord : splittedSearchString) {
                query.must(
                        queryBuilder
                                .keyword()
                                .wildcard()
                                .onField(FOOD_TITLE_FIELD_NAME).boostedTo(HIGH_PRIORITY)
                                .andField(FOOD_INGREDIENTS_FIELD_NAME).boostedTo(MEDIUM_PRIORITY)
                                .matching(getSearchValue(singleWord))
                                .createQuery()
                );
            }

            return query.createQuery();
        }

        if (entry.getKey().equals(INGREDIENTS_FILTER)) {
            List<Object> searchIngredients = entry.getValue();

            BooleanJunction<?> query = queryBuilder.bool();

            for (Object searshIngredient : searchIngredients) {
                if (searshIngredient == null || searshIngredient.toString().isBlank()) {
                    query.should(queryBuilder.all().createQuery());
                    continue;
                }
                String[] searshIngredientParts = parseSearchBarString(searshIngredient.toString());

                BooleanJunction<?> subQuery = queryBuilder.bool();

                for (String namePart : searshIngredientParts) {
                    subQuery.must(
                            queryBuilder
                                    .keyword()
                                    .wildcard()
                                    .onField(FOOD_INGREDIENTS_FIELD_NAME)
                                    .matching(getSearchValue(namePart))
                                    .createQuery()
                    );
                }

                query.should(subQuery.createQuery());
            }

            return query.createQuery();
        }

        return null;
    }

    protected String[] parseSearchBarString(String searchString) {
        return searchString
                .trim()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("(^\\s+|\\s+$)", "")
                .split("\\s+");
    }

    private String getSearchValue(String elm) {
        return elm.toLowerCase() + "*";
    }

    private QueryBuilder getQueryBuilder() {

        return getFullTextEntityManager()
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Food.class)
                .get();
    }

    private FullTextEntityManager getFullTextEntityManager() {
        return Search.getFullTextEntityManager(entityManager);
    }
}
