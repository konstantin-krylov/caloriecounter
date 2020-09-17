package com.epam.caloriecounter.service;

import com.epam.caloriecounter.dto.FillDatabaseRequest;
import com.epam.caloriecounter.dto.FillDatabaseResponse;
import com.epam.caloriecounter.dto.FoodDto;
import com.epam.caloriecounter.repository.FoodRepository;
import com.epam.caloriecounter.utils.RandomValueGenerator;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static com.epam.caloriecounter.TestHelper.readFileAsJsonString;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@Testcontainers
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "usda.service.url=http://localhost:4567/fdc/v1",
        "usda.service.key=testKey"
})
public class SystemServiceTestContainers {
    @Container
    public static final PostgreSQLContainer<?> DATABASE_CONTAINER = new PostgreSQLContainer<>();

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DATABASE_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    protected final ObjectMapper objectMapper = new ObjectMapper();

    private WireMockServer wireMockServer;

    @BeforeEach
    void configureSystemUnderTest() {
        this.wireMockServer = new WireMockServer(4567);
        this.wireMockServer.start();
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }

    private static final String USDA_API_GET_PATH = "/fdc/v1/food/%s?api_key=testKey&format=full&nutrients";

    @Autowired
    private SystemService systemService;

    @Autowired
    private FoodRepository foodRepository;

    @MockBean
    private RandomValueGenerator randomValueGenerator;

    @Test
    public void check_contextStarts() {
        assertAll(
                () -> assertThat(systemService).isNotNull(),
                () -> assertTrue(DATABASE_CONTAINER.isRunning())
        );
    }

    @Test
    void fill_shouldPersistNewFoodInDbExcludeDuplicates() throws JsonProcessingException, JSONException {
        configureFor("localhost", 4567);

        FillDatabaseRequest request = new FillDatabaseRequest().setNumberOfRecords(2);

        stubFor(get(urlEqualTo(String.format(USDA_API_GET_PATH, 477320)))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBodyFile("usda_stub_get_response_477320.json")));

        stubFor(get(urlEqualTo(String.format(USDA_API_GET_PATH, 459590)))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBodyFile("usda_stub_get_response_459590.json")));

        when(randomValueGenerator.generate())
                .thenReturn(477320)
                .thenReturn(477320)
                .thenReturn(459590);
        String expectedResponse = readFileAsJsonString("classpath:fill_db_response.json");

        FillDatabaseResponse fill = systemService.fill(request);
        String actualResponse = objectMapper.writeValueAsString(fill);

        JSONAssert.assertEquals(expectedResponse, actualResponse,
                new CustomComparator(JSONCompareMode.LENIENT,
                        new Customization("requestDuration", (o1, o2) -> true)));
        assertAll(
                () -> assertEquals(2, foodRepository.findAll().size()),
                () -> assertThat(foodRepository.findByFdcId(477320L))
        );

    }

}