package com.airports.airports.web;

import com.airports.airports.exceptions.ResourceNotFoundException;
import com.airports.airports.models.Flight;
import com.airports.airports.service.FlightService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flights")
public class FlightController {
    private final FlightService flightService;
    private final ResourceLoader resourceLoader;

    public FlightController(FlightService flightService, ResourceLoader resourceLoader) {
        this.flightService = flightService;
        this.resourceLoader = resourceLoader;
    }

    @PostMapping("/read-csv")
    public ResponseEntity<?> readFlightsFromCsv() {
        Resource resource = resourceLoader.getResource("classpath:/static/flights.csv");
        flightService.readFlightsFromCsv(resource);
        return ResponseEntity.ok("Flights file uploaded successfully.");

    }

    @GetMapping("/{code}/all")
    public ResponseEntity<?> getAllFlightsFromAirport(@PathVariable String code) throws JsonProcessingException {
        return ResponseEntity.ok(flightService.getAllFlightsFromAirport(code));
    }

    @GetMapping("/direct")
    public ResponseEntity<List<Flight>> getAllDirectFlights(
            @RequestParam("start") String startCode,
            @RequestParam("dest") String destCode) {
        return ResponseEntity.ok(flightService.getAllDirectFlights(startCode, destCode));
    }

    @GetMapping("/direct-to-destination")
    public ResponseEntity<List<Flight>> getAllDirectFlightsToAirport(@RequestParam("dest") String destCode) {
        return ResponseEntity.ok(flightService.getAllDirectFlightsToAirport(destCode));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable(value = "id") Long flightId,
                                               @Validated @RequestBody Flight flightDetails)
            throws ResourceNotFoundException {
        flightService.updateFlight(flightId, flightDetails);

        return ResponseEntity.ok(flightDetails);
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getFlights(@RequestParam Map<String, String> requestParams) {
        List<Flight> flights = flightService.getDynamicFlights(requestParams);
        return ResponseEntity.ok(flights);
    }
}