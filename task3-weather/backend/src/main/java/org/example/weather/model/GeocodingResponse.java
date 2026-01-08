package org.example.weather.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeocodingResponse {
    @SerializedName("results")
    private List<Location> results;

    public List<Location> getResults() {
        return results;
    }

    public static class Location {
        private String name;
        private double latitude;
        private double longitude;

        public String getName() {
            return name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}

