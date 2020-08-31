package com.epam.caloriecounter.gateway;

import com.epam.caloriecounter.dto.FoodItemResponse;
import com.epam.caloriecounter.dto.FoodSearchRequestDto;
import com.epam.caloriecounter.dto.FoodSearchResultResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UsdaApiGateway {

    @Value("${usda.url}")
    private String url;

    @Value("${usda.getEndpoint}")
    private String getEndpoint;

    @Value("${usda.searchEndpoint}")
    private String searchEndpoint;

    @Value("${usda.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public FoodItemResponse getFood(String fdcId, String format, List<Integer> nutrients) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url + getEndpoint)
                .queryParam("api_key", apiKey)
                .queryParam("format", format)
                .queryParam("nutrients", nutrients);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        URI uri = builder.buildAndExpand(fdcId).toUri();

        return restTemplate.exchange(uri, HttpMethod.GET, entity, FoodItemResponse.class).getBody();
    }

    public FoodSearchResultResponseDto search(FoodSearchRequestDto request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url + searchEndpoint)
                .queryParam("api_key", apiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(request, headers);

        return restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, FoodSearchResultResponseDto.class).getBody();
    }
}
