package org.example.weather.service;

import com.google.gson.Gson;
import org.example.weather.model.ForecastResponse;
import org.example.weather.model.GeocodingResponse;
import org.example.weather.model.WeatherData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeatherService {
    private static final String GEOCODING_API = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_API = "https://api.open-meteo.com/v1/forecast";
    private static final long CACHE_TIMEOUT_MS = 15 * 60 * 1000;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final Map<String, WeatherData> cache;
    private final Gson gson;

    public WeatherService() {
        this.cache = new ConcurrentHashMap<>();
        this.gson = new Gson();
    }

    public WeatherData getWeather(String city) throws Exception {
        String cityKey = city.toLowerCase().trim();

        WeatherData cached = cache.get(cityKey);
        if (cached != null && !cached.isExpired(CACHE_TIMEOUT_MS)) {
            return cached;
        }

        GeocodingResponse.Location location = getCityCoordinates(city);
        if (location == null) {
            throw new Exception("Город " + city + " не найден");
        }

        ForecastResponse forecast = getForecast(location.getLatitude(), location.getLongitude());
        if (forecast == null || forecast.getHourly() == null) {
            throw new Exception("Не удалось получить прогноз погоды");
        }
        
        String timezone = forecast.getTimezone();
        if (timezone == null || timezone.isEmpty()) {
            timezone = "UTC";
        }

        List<Double> temperatures = forecast.getHourly().getTemperature2m();
        List<String> times = forecast.getHourly().getTime();
        
        if (temperatures == null || times == null || temperatures.isEmpty() || times.isEmpty()) {
            throw new Exception("Нет данных о температуре");
        }

        ZonedDateTime nowInCityTimezone = ZonedDateTime.now(ZoneId.of(timezone));
        LocalDateTime currentHourInCity = nowInCityTimezone.withMinute(0).withSecond(0).withNano(0).toLocalDateTime();
        
        int startIndex = 0;
        double currentTemp = 0.0;
        DateTimeFormatter inputFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        DateTimeFormatter inputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter inputFormatter3 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (int i = 0; i < times.size(); i++) {
            String timeStr = times.get(i);
            LocalDateTime dateTime;
            try {
                if (timeStr.contains("T") && timeStr.length() == 16) {
                    dateTime = LocalDateTime.parse(timeStr, inputFormatter1);
                } else if (timeStr.contains("T") && timeStr.length() == 19) {
                    dateTime = LocalDateTime.parse(timeStr, inputFormatter2);
                } else {
                    dateTime = LocalDateTime.parse(timeStr, inputFormatter3);
                }

                if (dateTime.isEqual(currentHourInCity) || 
                    (dateTime.isAfter(currentHourInCity.minusMinutes(30)) && 
                     dateTime.isBefore(currentHourInCity.plusHours(1)))) {
                    currentTemp = temperatures.get(i);
                    startIndex = i;
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }


        if (startIndex == 0) {
            for (int i = 0; i < times.size(); i++) {
                String timeStr = times.get(i);
                LocalDateTime dateTime;
                try {
                    if (timeStr.contains("T") && timeStr.length() == 16) {
                        dateTime = LocalDateTime.parse(timeStr, inputFormatter1);
                    } else if (timeStr.contains("T") && timeStr.length() == 19) {
                        dateTime = LocalDateTime.parse(timeStr, inputFormatter2);
                    } else {
                        dateTime = LocalDateTime.parse(timeStr, inputFormatter3);
                    }

                    if (dateTime.isAfter(currentHourInCity) || dateTime.isEqual(currentHourInCity)) {
                        startIndex = i;
                        if (currentTemp == 0.0) {
                            currentTemp = temperatures.get(i);
                        }
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }

        if (currentTemp == 0.0 && !temperatures.isEmpty()) {
            currentTemp = temperatures.get(0);
        }

        int endIndex = Math.min(startIndex + 24, temperatures.size());
        List<Double> temp24h = new ArrayList<>();
        List<String> timeLabels = new ArrayList<>();
        
        for (int i = startIndex; i < endIndex; i++) {
            temp24h.add(temperatures.get(i));
            String timeStr = times.get(i);
            LocalDateTime dateTime;
            try {
                if (timeStr.contains("T") && timeStr.length() == 16) {
                    dateTime = LocalDateTime.parse(timeStr, inputFormatter1);
                } else if (timeStr.contains("T") && timeStr.length() == 19) {
                    dateTime = LocalDateTime.parse(timeStr, inputFormatter2);
                } else {
                    dateTime = LocalDateTime.parse(timeStr, inputFormatter3);
                }
                timeLabels.add(dateTime.format(TIME_FORMATTER));
            } catch (Exception e) {
                String[] parts = timeStr.split("T");
                if (parts.length > 1) {
                    String timePart = parts[1];
                    if (timePart.length() >= 5) {
                        timeLabels.add(timePart.substring(0, 5));
                    } else {
                        timeLabels.add(timeStr);
                    }
                } else {
                    timeLabels.add(timeStr);
                }
            }
        }

        WeatherData weatherData = new WeatherData(
            location.getName(),
            location.getLatitude(),
            location.getLongitude(),
            currentTemp,
            temp24h,
            timeLabels,
            timezone
        );

        cache.put(cityKey, weatherData);
        return weatherData;
    }

    private GeocodingResponse.Location getCityCoordinates(String city) throws Exception {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String urlString = GEOCODING_API + "?name=" + encodedCity + "&count=1&language=ru&format=json";
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Ошибка при получении координат: HTTP " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        GeocodingResponse geocodingResponse = gson.fromJson(response.toString(), GeocodingResponse.class);
        if (geocodingResponse.getResults() == null || geocodingResponse.getResults().isEmpty()) {
            return null;
        }

        return geocodingResponse.getResults().get(0);
    }

    private ForecastResponse getForecast(double latitude, double longitude) throws Exception {
        String urlString = FORECAST_API + "?latitude=" + latitude + "&longitude=" + longitude 
            + "&hourly=temperature_2m&forecast_days=2&timezone=auto";
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Ошибка при получении прогноза: HTTP " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return gson.fromJson(response.toString(), ForecastResponse.class);
    }

    public void clearCache() {
        cache.clear();
    }
}