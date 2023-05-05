package com.airports.airports.models.dto;

import lombok.Data;

@Data
public class AirportRequest {
    private String name;
    private String country;
    private String code;
    private int numPassengers;

    // Getters and setters omitted for brevity

    @Override
    public String toString() {
        return "AirportRequest{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", code='" + code + '\'' +
                ", numPassengers=" + numPassengers +
                '}';
    }
}