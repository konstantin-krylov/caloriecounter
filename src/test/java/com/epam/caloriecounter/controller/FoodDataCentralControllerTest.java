package com.epam.caloriecounter.controller;

import com.epam.caloriecounter.dto.FoodDto;
import com.epam.caloriecounter.dto.FoodNutrientDto;
import com.epam.caloriecounter.service.FoodDataCentralService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FoodDataCentralController.class)
class FoodDataCentralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FoodDataCentralService foodDataCentralService;

    @Test
    void check_contextStarts() {
        assertNotNull(mockMvc);
    }

    @Test
    void findOne_shouldReturnThePost() throws Exception {
        final FoodDto foodDto = new FoodDto()
                .setFoodId(364664L)
                .setFoodDescription("TestDescription")
                .setFoodIngredients("TestIngridients")
                .setFoodTitle("TestTitle")
                .setFoodType("Branded")
                .setFoodNutrients(Collections.singleton(new FoodNutrientDto()
                        .setAmount(34f)
                        .setNutrientName("Protein").setUnitName("mg")
                ));

        given(foodDataCentralService.saveFood("364664")).willReturn(foodDto);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://service-host/get-and-save")
                .queryParam("fdcId", 364664);

        mockMvc.perform(
                post(builder.toUriString())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("foodTitle").value(is("TestTitle")));
    }
}