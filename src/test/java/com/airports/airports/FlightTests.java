package com.airports.airports;

import com.airports.airports.models.Airport;
import com.airports.airports.models.Flight;
import com.airports.airports.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FlightTests {

    @Autowired
    private FlightRepository flightRepository;

    @Test
    public void testGetAllFlightsFromAirport() {
        List<Flight> flights = flightRepository.findByDestAirportCode("LAX");
        assertNotNull(flights);
        assertTrue(flights.size() > 0);
    }

    @Test
    public void testGetAllDirectFlights() {
        List<Flight> flights = flightRepository.findByStartAirportCodeAndDestAirportCode("LAX", "JFK");
        assertNotNull(flights);
        assertTrue(flights.size() > 0);
    }

    @Test
    public void testGetAllDirectFlightsToAirport() {
        List<Flight> flights = flightRepository.findByDestAirportCode("JFK");
        assertNotNull(flights);
        assertTrue(flights.size() > 0);
    }

    @Test
    public void testCreateFlight() {
        Flight flight = new Flight();
        Airport startAirport = new Airport();
        startAirport.setCode("JFK");
        startAirport.setName("John F. Kennedy International Airport");
        startAirport.setCountry("USA");
        startAirport.setNumPassengers(50);

        Airport destAirport = new Airport();
        startAirport.setCode("SKP");
        startAirport.setName("Skopje International Airport");
        startAirport.setCountry("NMK");
        startAirport.setNumPassengers(100);

        flight.setDuration(400);
        flight.setDepartureTime(200);
        flight.setDestAirport(destAirport);
        flight.setStartAirport(startAirport);
        flightRepository.save(flight);

        Optional<Flight> createdFlight = flightRepository.getExistingFlight(400, 200, destAirport.getCode(), startAirport.getCode());
        assertNotNull(createdFlight);
    }

}