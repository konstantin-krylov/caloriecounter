package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FoodDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static com.epam.caloriecounter.TestHelper.readFileAsJsonString;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test interaction (Service -> DataBase) with USDA API stub
 */
@Testcontainers
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "usda.service.url=http://localhost:4567/fdc/v1",
        "usda.service.key=testKey"
})
public class FoodServiceTestContainers {
    @Container
    public static final PostgreSQLContainer DATABASE_CONTAINER = new PostgreSQLContainer();

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DATABASE_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    protected final ObjectMapper objectMapper = new ObjectMapper();

    private final WireMockServer wireMockServer = new WireMockServer(4567);

    private static final String USDA_API_GET_PATH = "/fdc/v1/food/%s?api_key=testKey&format=full&nutrients";

    @Autowired
    private FoodDataCentralService foodDataCentralService;

    @Test
    public void check_contextStarts() {
        assertAll(
                () -> assertThat(foodDataCentralService).isNotNull(),
                () -> assertTrue(DATABASE_CONTAINER.isRunning())
        );
    }

    @Test
    void saveFood_shouldGetFoodFromUsdaApiAndPersistNewFoodInDB() throws JsonProcessingException, JSONException {

        wireMockServer.start();
        configureFor("localhost", 4567);

        stubFor(get(urlEqualTo(String.format(USDA_API_GET_PATH, 477320)))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBodyFile("usda_stub_get_response.json")));

        FoodDto foodDto = foodDataCentralService.saveFood("477320");

        String actualResponse = objectMapper.writeValueAsString(foodDto);
        String expectedResponse = readFileAsJsonString("classpath:food_dto_response.json");

        JSONAssert.assertEquals(expectedResponse,
                actualResponse,
                JSONCompareMode.NON_EXTENSIBLE);

        wireMockServer.stop();
    }

}