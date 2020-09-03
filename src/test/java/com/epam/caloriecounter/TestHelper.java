package com.epam.caloriecounter;

import lombok.SneakyThrows;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStreamReader;
import java.io.Reader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TestHelper {

    private final RestTemplate restTemplate = new RestTemplate();

    @SneakyThrows
    public static String readObjectAsJsonString(final Object obj) {
        return new ObjectMapper().writeValueAsString(obj);
    }

    @SneakyThrows
    public static String readFileAsJsonString(String location) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(location);

        Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8);
        return FileCopyUtils.copyToString(reader);
    }

    protected ResponseEntity<Object> sendPostRestRequest(String requestPath, String requestJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForEntity(requestPath, new HttpEntity<>(requestJson, headers), Object.class);
    }
}
