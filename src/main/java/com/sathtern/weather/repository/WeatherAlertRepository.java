package com.sathtern.weather.repository;

import com.sathtern.weather.model.WeatherAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeatherAlertRepository extends JpaRepository<WeatherAlert, Long> {
    List<WeatherAlert> findByUserId(Long userId);
    List<WeatherAlert> findByIsActiveTrue();
}