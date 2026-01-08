package org.example.weather.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {
    @SerializedName("hourly")
    private HourlyData hourly;
    private String timezone;

    public HourlyData getHourly() {
        return hourly;
    }

    public String getTimezone() {
        return timezone;
    }

    public static class HourlyData {
        private List<String> time;
        @SerializedName("temperature_2m")
        private List<Double> temperature2m;

        public List<String> getTime() {
            return time;
        }

        public List<Double> getTemperature2m() {
            return temperature2m;
        }
    }
}

