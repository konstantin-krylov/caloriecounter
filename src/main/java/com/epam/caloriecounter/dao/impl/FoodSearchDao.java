package com.epam.caloriecounter.dao.impl;//package com.epam.caloriecounter.dao.impl;

import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.mapper.FoodMapper;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.Query;
import org.hibernate.search.engine.ProjectionConstants;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FoodSearchDao {

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
                .onField("foodIngredients")
                .matching(text)
                .createQuery();

        @SuppressWarnings("unchecked")
        List<Food> resultList = (List<Food>) getJpaQuery(fuzzyQuery).getResultList();

        return resultList;
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
