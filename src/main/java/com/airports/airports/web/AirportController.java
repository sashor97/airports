package com.airports.airports.web;

import com.airports.airports.models.Airport;
import com.airports.airports.models.dto.AirportRequest;
import com.airports.airports.service.AirportService;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/airports")
public class AirportController {
    private final AirportService airportService;

    private final ResourceLoader resourceLoader;

    public AirportController(AirportService airportService, ResourceLoader resourceLoader) {
        this.airportService = airportService;
        this.resourceLoader = resourceLoader;
    }

    @PostMapping("/read-csv")
    public ResponseEntity<?> uploadAirportsFile() {
        Resource resource = resourceLoader.getResource("classpath:/static/airports.csv");
        airportService.readAirportsFromCSV(resource);
        return ResponseEntity.ok("Airports file uploaded successfully.");
    }

    @PostMapping
    public ResponseEntity<?> addAirport(@RequestBody AirportRequest request) {
        try {
            Airport airport = airportService.addAirport(request.getName(), request.getCountry(),
                    request.getCode(), request.getNumPassengers());
            return ResponseEntity.ok(airport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{country}/mostPassengers")
    public ResponseEntity<?> getAirportWithMostPassengersForCountry(@PathVariable String country) {
        try {

            Airport airport = airportService.getAirportWithMostPassengersForCountry(country);
            return ResponseEntity.ok(airport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteAirport(@PathVariable String code) {
        try {
            airportService.deleteAirport(code);
            return ResponseEntity.ok("Airport with code " + code + " deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}