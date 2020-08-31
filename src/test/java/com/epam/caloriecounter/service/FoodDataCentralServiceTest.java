package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodSearchRequestDto;
import com.epam.caloriecounter.dto.FoodSearchResultResponseDto;
import com.epam.caloriecounter.gateway.UsdaApiGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(UsdaApiGateway.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@TestPropertySource(properties = {
        "usda.url=http://localhost:4567/fdc/v1",
        "usda.key=testKey"
})
class FoodDataCentralServiceTest {

    @Autowired
    private UsdaApiGateway usdaApiGateway;

    @Autowired
    private MockRestServiceServer restServer;

    @Value("classpath:usda_get_response.json")
    private Resource usdaGetResponse;

    @Value("classpath:usda_search_response.json")
    private Resource usdaSearchResponse;

    @Test
    public void check_contextStarts() {
        assertAll(
                () -> assertNotNull(usdaApiGateway),
                () -> assertNotNull(restServer)
        );
    }

    @Test
    public void get_withMockRestServer() {
        restServer.expect(requestTo("http://localhost:4567/fdc/v1/food/170844?api_key=testKey&format=full&nutrients=203&nutrients=204&nutrients=205&nutrients=208"))
                .andRespond(withSuccess(usdaGetResponse, MediaType.APPLICATION_JSON));

        FoodItemResponse foodItemResponse = usdaApiGateway.getFood("170844", "full", Arrays.asList(203, 204, 205, 208));

        assertAll(
                () -> assertNotNull(foodItemResponse),
                () -> assertEquals(4, foodItemResponse.getFoodNutrients().size())
        );
    }

    @Test
    public void search_withMockRestServer() {
        restServer.expect(requestTo("http://localhost:4567/fdc/v1/foods/search?api_key=testKey"))
                .andRespond(withSuccess(usdaSearchResponse, MediaType.APPLICATION_JSON));

        FoodSearchRequestDto request = new FoodSearchRequestDto()
                .setQuery("Cheese")
                .setBrandOwner("+Sartori")
                .setDataType(Collections.singletonList("Branded"))
                .setPageNumber(1)
                .setPageSize(3)
                .setSortBy("dataType.keyword")
                .setSortOrder("asc");
        FoodSearchResultResponseDto searchResult = usdaApiGateway.search(request);

        assertAll(
                () -> assertNotNull(searchResult),
                () -> assertEquals(3, searchResult.getSearchResultFoods().size())
        );

    }

}