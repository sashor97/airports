package com.airports.airports;

import com.airports.airports.models.Airport;
import com.airports.airports.repository.AirportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AirportTests {

    @Autowired
    private AirportRepository airportRepository;

    @Test
    public void testGetAirportByCode() {
        Optional<Airport> airport = airportRepository.findByCode("LAX");
        airport.ifPresent(value->assertEquals("Los Angeles International Airport",value.getName()));

    }

    @Test
    public void testGetAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        assertNotNull(airports);
        assertTrue(airports.size() > 0);
    }

    @Test
    public void testCreateAirport() {
        Airport airport = new Airport();
        airport.setCode("JFK");
        airport.setName("John F. Kennedy International Airport");
        airport.setCountry("USA");
        airport.setNumPassengers(50);
        airportRepository.save(airport);

        Optional<Airport> createdAirport = airportRepository.findByCode("JFK");
        assertNotNull(createdAirport);
        createdAirport.ifPresent(value -> assertEquals("John F. Kennedy International Airport", value.getName()));
    }

    @Test
    public void testUpdateAirport() {
        Optional<Airport> airport = airportRepository.findByCode("LAX");
        airport.ifPresent(value -> {
            value.setName("Los Angeles International Airport (LAX)");
            airportRepository.save(value);
        });
        Optional<Airport> updatedAirport = airportRepository.findByCode("LAX");
        assertNotNull(updatedAirport);
        updatedAirport.ifPresent(value -> assertEquals("Los Angeles International Airport (LAX)", value.getName()));
    }

    @Test
    public void testDeleteAirport() {
        Optional<Airport> airport = airportRepository.findByCode("LAX");
        airport.ifPresent(value -> airportRepository.delete(value));

        Optional<Airport> deletedAirport = airportRepository.findByCode("LAX");
        assertNull(deletedAirport);
    }
}