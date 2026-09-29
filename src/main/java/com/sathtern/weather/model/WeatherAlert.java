package com.sathtern.weather.model;

import jakarta.persistence.*;

@Entity
@Table(name = "weather_alerts")
public class WeatherAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cityName;

    @Enumerated(EnumType.STRING)
    private AlertType type;

    private Double threshold;

    private Boolean isActive = true;
    private Boolean triggered = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum AlertType {
        RAIN, HEAT, COLD, WIND
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public AlertType getType() { return type; }
    public void setType(AlertType type) { this.type = type; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getTriggered() { return triggered; }
    public void setTriggered(Boolean triggered) { this.triggered = triggered; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}