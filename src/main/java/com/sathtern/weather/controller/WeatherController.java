package com.sathtern.weather.controller;

import com.sathtern.weather.dto.WeatherResponseDto;
import com.sathtern.weather.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public WeatherResponseDto getCurrentWeather(@RequestParam String city) {
        return weatherService.getCurrentWeather(city);
    }
}