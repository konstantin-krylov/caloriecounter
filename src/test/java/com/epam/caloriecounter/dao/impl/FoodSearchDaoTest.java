package com.epam.caloriecounter.dao.impl;

import com.epam.caloriecounter.dao.hibernatesearch.SearchRequest;
import com.epam.caloriecounter.dto.ShortFoodDto;
import com.epam.caloriecounter.repository.FoodRepository;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FoodSearchDaoTest {

    @Container
    public static final PostgreSQLContainer<?> DATABASE_CONTAINER = new PostgreSQLContainer<>("postgres:9.5.10")
            .withInitScript("db/test-data-migration-script.sql");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DATABASE_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private FoodSearchDao foodSearchDao;

    public static final String DIFFICULT_SEARCH_BAR = "  MILK,  CREAM,   SKIM MILK:,    (SUGAR}, BUTTERMILK, WHEY.  ";

    @Test
    void check_contextStarts() {
        assertAll(
                () -> assertThat(foodRepository).isNotNull(),
                () -> assertThat(foodRepository.findAll().size() == 301),
                () -> assertTrue(DATABASE_CONTAINER.isRunning())
        );
    }

    @Test
    void search_shouldReturnPageOfFoodByIngridientFilter() {
        Map<String, List<Object>> query = new HashMap<>();
        List<Object> ingredients = new ArrayList<>();
        ingredients.add("water");
        query.put("ingredients", ingredients);

        SearchRequest searchRequest = new SearchRequest(3, 25, query, null);
        Page<ShortFoodDto> responseList = foodSearchDao.search(searchRequest);

        MatcherAssert.assertThat(responseList.getNumberOfElements(), is(25));
        MatcherAssert.assertThat(responseList.getTotalElements(), is(127L));
        MatcherAssert.assertThat(responseList.getTotalPages(), is(6));
        MatcherAssert.assertThat(responseList.stream()
                        .allMatch(food -> food.getFoodIngredients()
                                .toLowerCase()
                                .contains("water")),
                is(true));
    }

    @Test
    void search_shouldReturnPageOfFoodBySeveralIngridientsFilter() {
        Map<String, List<Object>> query = new HashMap<>();
        List<Object> ingredients = new ArrayList<>();
        ingredients.add("water SALT POpPy"); // return all elements that contains water and salt and poppy simultaneously
        ingredients.add(" POTATO   JUICE/ OIL, Corn Beef "); // and all elements with another filter set
        query.put("ingredients", ingredients);

        SearchRequest searchRequest = new SearchRequest(0, 25, query, null);
        Page<ShortFoodDto> responseList = foodSearchDao.search(searchRequest);

        MatcherAssert.assertThat(responseList.getNumberOfElements(), is(2));
        MatcherAssert.assertThat(responseList.getTotalElements(), is(2L));
        MatcherAssert.assertThat(responseList.getTotalPages(), is(1));
        MatcherAssert.assertThat(responseList.stream()
                .map(ShortFoodDto::getFoodId)
                .collect(Collectors.toList()), is(Arrays.asList(116L, 163L)));
    }

    @Test
    void search_shouldReturnPageOfFoodBySearchBarRequest() {
        SearchRequest searchRequest = new SearchRequest(0, 25, null, DIFFICULT_SEARCH_BAR);
        Page<ShortFoodDto> responseList = foodSearchDao.search(searchRequest);

        MatcherAssert.assertThat(responseList.getNumberOfElements(), is(1));
        MatcherAssert.assertThat(responseList.getTotalElements(), is(1L));
        MatcherAssert.assertThat(responseList.getTotalPages(), is(1));
        MatcherAssert.assertThat(responseList.getContent().get(0).getFoodTitle(), is("COFFEE ICE CREAM, MOCHA ME HOPPY"));
    }

    @Test
    void search_shouldReturnPageAllFoodByRequestWithoutAnyFilterOrSearchBarString() {
        SearchRequest searchRequest = new SearchRequest(0, 25, null, null);
        Page<ShortFoodDto> responseList = foodSearchDao.search(searchRequest);

        MatcherAssert.assertThat(responseList.getNumberOfElements(), is(25));
        MatcherAssert.assertThat(responseList.getTotalElements(), is(301L));
        MatcherAssert.assertThat(responseList.getTotalPages(), is(13));
    }

    @Test
    void search_shouldReturnPageAllFoodByRequestWithEmptyAndNullFilter() {
        Map<String, List<Object>> query = new HashMap<>();
        List<Object> ingredients = new ArrayList<>();
        ingredients.add("");
        ingredients.add(null);
        query.put("ingredients", ingredients);

        SearchRequest searchRequest = new SearchRequest(0, 25, query, null);
        Page<ShortFoodDto> responseList = foodSearchDao.search(searchRequest);

        MatcherAssert.assertThat(responseList.getNumberOfElements(), is(25));
        MatcherAssert.assertThat(responseList.getTotalElements(), is(301L));
        MatcherAssert.assertThat(responseList.getTotalPages(), is(13));
    }

    @Test
    void search_shouldReturnPageAllFoodByRequestWithNonExistingFilter() {
        Map<String, List<Object>> query = new HashMap<>();
        List<Object> ingredients = new ArrayList<>();
        ingredients.add("somethong");
        query.put("notExistingFilterName", ingredients);

        SearchRequest searchRequest = new SearchRequest(0, 0, query, null);
        Page<ShortFoodDto> responseList = foodSearchDao.search(searchRequest);

        MatcherAssert.assertThat(responseList.getNumberOfElements(), is(10));
        MatcherAssert.assertThat(responseList.getTotalElements(), is(301L));
        MatcherAssert.assertThat(responseList.getTotalPages(), is(31));
    }

    @Test
    void search_shouldReturnPageAllFoodByRequestWithEmptySearchBarString() {
        SearchRequest searchRequest = new SearchRequest(0, 0, null, "");
        Page<ShortFoodDto> responseList = foodSearchDao.search(searchRequest);

        MatcherAssert.assertThat(responseList.getNumberOfElements(), is(10));
        MatcherAssert.assertThat(responseList.getTotalElements(), is(301L));
        MatcherAssert.assertThat(responseList.getTotalPages(), is(31));
    }

    @Test
    void parseSearchBarString_shouldReplaceAllNonWordCharacters() {
        String[] actualFoodArray = foodSearchDao.parseSearchBarString(DIFFICULT_SEARCH_BAR);
        String[] expectedFoodArray = {"MILK", "CREAM", "SKIM", "MILK", "SUGAR", "BUTTERMILK", "WHEY"};
        assertArrayEquals(expectedFoodArray, actualFoodArray);
    }

}