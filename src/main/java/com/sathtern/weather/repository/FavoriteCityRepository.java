package com.sathtern.weather.repository;

import com.sathtern.weather.model.FavoriteCity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteCityRepository extends JpaRepository<FavoriteCity, Long> {
    List<FavoriteCity> findByUserId(Long userId);
}