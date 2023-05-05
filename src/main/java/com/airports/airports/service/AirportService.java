package com.airports.airports.service;

import com.airports.airports.exceptions.AirportException;
import com.airports.airports.exceptions.AirportNotFoundException;
import com.airports.airports.exceptions.NonUniqueResultException;
import com.airports.airports.models.Airport;
import com.airports.airports.repository.AirportRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AirportService {
    private final AirportRepository airportRepository;
    private final EntityManager entityManager;
    private static final Logger log = LoggerFactory.getLogger(AirportService.class);

    public AirportService(AirportRepository airportRepository, EntityManager entityManager) {
        this.airportRepository = airportRepository;
        this.entityManager = entityManager;
    }

    public void readAirportsFromCSV(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build(); // skip header line
            List<String[]> rows = csvReader.readAll();

            // Get the list of all airports in the database
            List<Airport> airports = airportRepository.findAll();

            deleteInactiveAirports(airports, rows);

            iterateRowsAndStoreInDatabase(rows);


        } catch (IOException e) {
            log.error(e.getMessage());
            // handle the exception
        } catch (CsvException e) {
            log.error(e.getMessage());
        }


    }


    public Airport addAirport(String name, String country, String code, int numPassengers) {
        Airport airport = Airport.builder().name(name).country(country).code(code).numPassengers(numPassengers).build();
        return airportRepository.save(airport);
    }


    public Airport getAirportWithMostPassengersForCountry(String country) {
        try {
            Query query = entityManager.createQuery("SELECT a FROM Airport a WHERE a.country = :country ORDER BY a.numPassengers DESC");
            query.setParameter("country", country);
            query.setMaxResults(1);
            return (Airport) query.getSingleResult();

        } catch (NoResultException e) {
            throw new AirportNotFoundException("No airport found for country: " + country, e);
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("Multiple airports found for country: " + country, e);
        } catch (Exception e) {
            throw new AirportException("Failed to retrieve airport for country: " + country, e);
        }
    }

    public void deleteAirport(String code) {
        Airport airport = airportRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found"));
        airportRepository.delete(airport);
    }

    public void iterateRowsAndStoreInDatabase(List<String[]> rows) {
        for (String[] row : rows) {
            // process each record
            String[] values = row[0].split(";");

            String airportName = values[0];
            String countryName = values[1];
            String airportCode = values[2];
            int passengers = Integer.parseInt(values[3]);

            try {
                Optional<Airport> airport = airportRepository.findByCode(airportCode);
                if (airport.isPresent()) {
                    airportRepository.updateAirport(airportName, countryName, passengers);
                } else {
                    addAirport(airportName, countryName, airportCode, passengers);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            System.out.println(Arrays.toString(row));
        }
    }

    public void deleteInactiveAirports(List<Airport> airports, List<String[]> rows) {
        // Delete inactive airports
        for (Airport airport : airports) {
            String code = airport.getCode();
            boolean isActive = rows.stream().anyMatch(
                    row -> row[0].split(";")[2].equals(code)
            );
            if (!isActive) {
                airportRepository.delete(airport);
            }
        }
    }


}
