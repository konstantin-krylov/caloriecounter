package com.epam.caloriecounter.repository;

import com.epam.caloriecounter.entity.Food;
import com.epam.caloriecounter.entity.FoodNutrient;
import com.epam.caloriecounter.entity.FoodType;
import com.epam.caloriecounter.entity.NutrientType;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test interaction SpringRepository <--> DataBase
 */
@Testcontainers
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FoodRepositoryTestContainers {
    @Container
    public static final PostgreSQLContainer DATABASE_CONTAINER = new PostgreSQLContainer();

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DATABASE_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private FoodRepository foodRepository;

    @Test
    public void check_contextStarts() {
        assertAll(
                () -> assertThat(foodRepository).isNotNull(),
                () -> assertTrue(DATABASE_CONTAINER.isRunning())
        );
    }

    @Test
    void saveAndFindById_shouldPersistNewFoodInDB() {

        final Food food = new Food();
        food.setFoodId(1L);
        food.setFoodTitle("TestFoodTitle");
        food.setFoodType(new FoodType().setFoodTypeTitle("TestFoodType"));
        food.setFoodDescription("TestDescription");
        food.setFoodIngredients("Test, Test, Test");
        food.setFoodNutrients(Collections.singleton(new FoodNutrient()
                .setFood(food)
                .setNutrientId(2L)
                .setAmount(2.5f)
                .setNutrientType(new NutrientType()
                        .setUnitName("TestNutrientUnit")
                        .setNutrientName("Protein")
                        .setNutrientTypeId(3L))

        ));

        final Food savedFood = foodRepository.save(food);
        assertThat(savedFood).isNotNull();

        final Food foundFood = foodRepository.findById(savedFood.getFoodId()).orElseThrow();
        assertAll(
                () -> assertThat(foundFood).isNotNull(),
                () -> assertEquals("TestFoodTitle", foundFood.getFoodTitle())
        );
    }


}