package org.example.weather.model;

import java.util.List;

public class WeatherData {
    private final String city;
    private final double latitude;
    private final double longitude;
    private final double currentTemperature;
    private final List<Double> temperatures;
    private final List<String> timeLabels;
    private final String timezone;
    private final long timestamp;

    public WeatherData(String city, double latitude, double longitude, double currentTemperature, List<Double> temperatures, List<String> timeLabels, String timezone) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentTemperature = currentTemperature;
        this.temperatures = temperatures;
        this.timeLabels = timeLabels;
        this.timezone = timezone;
        this.timestamp = System.currentTimeMillis();
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public List<Double> getTemperatures() {
        return temperatures;
    }

    public List<String> getTimeLabels() {
        return timeLabels;
    }

    public String getTimezone() {
        return timezone;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired(long cacheTimeoutMillis) {
        return (System.currentTimeMillis() - timestamp) > cacheTimeoutMillis;
    }
}

