package com.sathtern.weather.controller;

import com.sathtern.weather.model.FavoriteCity;
import com.sathtern.weather.model.User;
import com.sathtern.weather.repository.FavoriteCityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteCityController {

    private final FavoriteCityRepository favoriteCityRepository;

    public FavoriteCityController(FavoriteCityRepository favoriteCityRepository) {
        this.favoriteCityRepository = favoriteCityRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping
    public List<FavoriteCity> getFavorites(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return favoriteCityRepository.findByUserId(user.getId());
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody Map<String, String> body, Authentication authentication) {
        User user = getCurrentUser(authentication);

        FavoriteCity city = new FavoriteCity();
        city.setCityName(body.get("cityName"));
        city.setUser(user);

        favoriteCityRepository.save(city);
        return ResponseEntity.ok(city);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavorite(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);

        return favoriteCityRepository.findById(id)
                .filter(city -> city.getUser().getId().equals(user.getId()))
                .map(city -> {
                    favoriteCityRepository.delete(city);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}