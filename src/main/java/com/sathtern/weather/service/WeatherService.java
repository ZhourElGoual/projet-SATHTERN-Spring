package com.sathtern.weather.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.sathtern.weather.dto.WeatherResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.url}")
    private String apiUrl;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "weatherCache", key = "#city")
    public WeatherResponseDto getCurrentWeather(String city) {
        String url = String.format("%s/weather?q=%s&appid=%s&units=metric&lang=fr",
                apiUrl, city, apiKey);

        String response = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        return parseWeatherResponse(response);
    }

    private WeatherResponseDto parseWeatherResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            WeatherResponseDto dto = new WeatherResponseDto();

            dto.setCityName(root.path("name").asText());
            dto.setTemperature(root.path("main").path("temp").asDouble());
            dto.setFeelsLike(root.path("main").path("feels_like").asDouble());
            dto.setHumidity(root.path("main").path("humidity").asInt());
            dto.setWindSpeed(root.path("wind").path("speed").asDouble());

            JsonNode weatherArray = root.path("weather");
            if (weatherArray.isArray() && weatherArray.size() > 0) {
                dto.setDescription(weatherArray.get(0).path("description").asText());
                dto.setIcon(weatherArray.get(0).path("icon").asText());
            }

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du parsing de la réponse météo: " + e.getMessage());
        }
    }
}