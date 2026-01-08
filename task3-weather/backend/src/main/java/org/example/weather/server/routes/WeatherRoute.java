package org.example.weather.server.routes;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.weather.model.WeatherData;
import org.example.weather.service.WeatherService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WeatherRoute implements HttpHandler {
    private final WeatherService weatherService;
    private final Gson gson;

    public WeatherRoute(WeatherService weatherService) {
        this.weatherService = weatherService;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        if (exchange.getRequestMethod().equals("OPTIONS")) {
            handleCors(exchange);
            return;
        }

        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.contains("city=")) {
                sendError(exchange, 400, "Параметр city не указан");
                return;
            }

            String city = extractCityFromQuery(query);
            if (city == null || city.isEmpty()) {
                sendError(exchange, 400, "Название города не указано");
                return;
            }

            WeatherData weatherData = weatherService.getWeather(city);
            String jsonResponse = gson.toJson(weatherData);
            
            sendResponse(exchange, 200, jsonResponse);
        } catch (Exception e) {
            sendError(exchange, 500, "Ошибка при получении данных о погоде: " + e.getMessage());
        }
    }

    private String extractCityFromQuery(String query) {
        String[] params = query.split("&");
        for (String param : params) {
            if (param.startsWith("city=")) {
                String city = param.substring(5);
                return java.net.URLDecoder.decode(city, StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private void handleCors(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        setCorsHeaders(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        setCorsHeaders(exchange);
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        String jsonError = gson.toJson(error);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = jsonError.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}

