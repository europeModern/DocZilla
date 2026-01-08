package org.example.weather.server;

import com.sun.net.httpserver.HttpServer;
import org.example.weather.service.WeatherService;
import org.example.weather.server.routes.WeatherRoute;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WeatherServer {
    private static final int PORT = 8081;
    private final WeatherService weatherService;
    private HttpServer server;

    public WeatherServer() {
        this.weatherService = new WeatherService();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        server.createContext("/weather", new WeatherRoute(weatherService));
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Weather Server запущен на http://localhost:" + PORT);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Weather Server остановлен");
        }
    }

    public static void main(String[] args) {
        WeatherServer weatherServer = new WeatherServer();
        try {
            weatherServer.start();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                weatherServer.stop();
            }));
            
            System.out.println("Нажмите Enter для остановки сервера...");
            System.in.read();
            weatherServer.stop();
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

