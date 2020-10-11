package com.epam.caloriecounter.dao.impl;//package com.epam.caloriecounter.dao.impl;

import com.epam.caloriecounter.dao.hibernatesearch.SearchRequest;
import com.epam.caloriecounter.dto.CustomSort;
import com.epam.caloriecounter.entity.Food;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.engine.ProjectionConstants;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Component
@RequiredArgsConstructor
public class FoodSearchDao {

    private static final String SEARCH_BAR_REQUEST = "searchBar";
    private static final String DATA_TYPE_FILTER = "dataType";
    private static final String INGREDIENTS_FILTER = "ingredients";

    private static final String FOOD_TITLE_FIELD_NAME = "foodTitle";
    public static final String FOOD_INGREDIENTS_FIELD_NAME = "foodIngredients";
    public static final String FOOD_TYPE_TITLE_FIELD_NAME = "foodTypeTitle";

    private static final float HIGH_PRIORITY = 5F;
    private static final float MEDIUM_PRIORITY = 3F;
    private static final float LOW_PRIORITY = 1F;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> searchProductNameByMoreLikeThisQuery(Food entity) {

        Query moreLikeThisQuery = getQueryBuilder()
                .moreLikeThis()
                .comparingField("foodTitle")
                .toEntity(entity)
                .createQuery();

        @SuppressWarnings("unchecked")
        List<Object[]> results = getJpaQuery(moreLikeThisQuery)
                .setProjection(ProjectionConstants.THIS, ProjectionConstants.SCORE)
                .getResultList();

        return results;
    }

    public List<Food> searchFoodNameByFuzzyQuery(String text) {

        Query fuzzyQuery = getQueryBuilder()
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(0)
                .onField(FOOD_INGREDIENTS_FIELD_NAME)
                .matching(text)
                .createQuery();

        @SuppressWarnings("unchecked")
        List<Food> resultList = (List<Food>) getJpaQuery(fuzzyQuery).getResultList();

        return resultList;
    }

    public List<Food> search(SearchRequest searchRequest) {

        Map<String, List<Object>> filterParams = addSearchBarQueryToFilters(searchRequest);

        FullTextQuery fullTextQuery = createFullTextQueryFromFilters(filterParams, getFullTextEntityManager(), getQueryBuilder());

        fullTextQuery.setFirstResult(fullTextQuery.getFirstResult());
        fullTextQuery.setMaxResults(fullTextQuery.getResultSize());
        fullTextQuery.setSort(getLuceneSort(searchRequest.getSorting()));

        @SuppressWarnings("unchecked")
        List<Food> resultList = (List<Food>) fullTextQuery.getResultList();

        return resultList;

    }

    private Sort getLuceneSort(CustomSort sort) {
        SortField[] sortFields = new SortField[sort.getProperties().size()];
        for (int i = 0; i < sort.getProperties().size(); i++) {
            sortFields[i] = new SortField(
                    sort.getProperties().get(i),
                    SortField.Type.STRING, sort.getDirection().equals(DESC.toString())
            );
        }

        return new Sort(sortFields);
    }

    private FullTextQuery createFullTextQueryFromFilters(Map<String, List<Object>> filterParams,
                                                         FullTextEntityManager fullTextEntityManager,
                                                         QueryBuilder queryBuilder) {
        if (filterParams == null || filterParams.isEmpty()) {
            return fullTextEntityManager.createFullTextQuery(queryBuilder.all().createQuery(), Food.class);
        }

        BooleanJunction<?> query = queryBuilder.bool();

        for (Map.Entry<String, List<Object>> entry : filterParams.entrySet()) {

            if (StringUtils.isNotBlank(entry.getKey()) || entry.getValue() != null) {
                Query customQuery = processCustomFilter(entry, queryBuilder);
                if (customQuery != null) {
                    query.must(customQuery);
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return fullTextEntityManager.createFullTextQuery(query.createQuery(), Food.class);
    }

    private Query processCustomFilter(Map.Entry<String, List<Object>> entry, QueryBuilder queryBuilder) {
        if (entry.getKey().equals(SEARCH_BAR_REQUEST)) {
            String[] splittedSearchString = parseSearchBarString(entry);

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
            List<Object> foodTypeTitles = entry.getValue();

            BooleanJunction<?> query = queryBuilder.bool();

            for (Object foodTypeTitle : foodTypeTitles) {

                String[] foodTypeTitleParts = foodTypeTitle.toString().trim().split(" ");

                BooleanJunction<?> subQuery = queryBuilder.bool();

                for (String namePart : foodTypeTitleParts) {
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

    private String[] parseSearchBarString(Map.Entry<String, List<Object>> entry) {
        String searchString = (String) entry.getValue().get(0);
        return searchString
                .trim()
                .replaceAll("(^\\s+|\\s+$)", "")
                .split("\\s+");
    }

    protected String getSearchValue(String elm) {
        return elm.toLowerCase() + "*";
    }

    protected Map<String, List<Object>> addSearchBarQueryToFilters(SearchRequest searchRequest) {

        String searchBarQuery = searchRequest.getSearchBar();

        if (StringUtils.isNotBlank(searchBarQuery)) {

            Map<String, List<Object>> filters = searchRequest.getFilters();
            filters.put(SEARCH_BAR_REQUEST, Collections.singletonList(searchBarQuery));
            return filters;

        } else {
            return searchRequest.getFilters();
        }
    }

    private QueryBuilder getQueryBuilder() {

        return getFullTextEntityManager()
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Food.class)
                .get();
    }

    private FullTextQuery getJpaQuery(Query luceneQuery) {

        return getFullTextEntityManager()
                .createFullTextQuery(luceneQuery, Food.class);
    }

    private FullTextEntityManager getFullTextEntityManager() {
        return Search.getFullTextEntityManager(entityManager);
    }
}
