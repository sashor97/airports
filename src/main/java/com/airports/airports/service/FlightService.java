package com.airports.airports.service;

import com.airports.airports.exceptions.ResourceNotFoundException;
import com.airports.airports.models.Airport;
import com.airports.airports.models.Flight;
import com.airports.airports.repository.AirportRepository;
import com.airports.airports.repository.FlightRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.*;
import java.util.*;

@Service
public class FlightService {

    private static final Logger log = LoggerFactory.getLogger(AirportService.class);

    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;

    private final EntityManager entityManager;


    public FlightService(AirportRepository airportRepository, FlightRepository flightRepository, EntityManager entityManager) {
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
        this.entityManager = entityManager;
    }

    public void readFlightsFromCsv(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build(); // skip header line
            List<String[]> rows = csvReader.readAll();

            List<Flight> flights = flightRepository.findAll();

            deleteInactiveFlights(flights, rows);


            iterateRowsAndStoreInDatabase(rows);


        } catch (IOException e) {
            log.error(e.getMessage());
            // handle the exception
        } catch (CsvException e) {
            log.error(e.getMessage());
        }


    }

    private void iterateRowsAndStoreInDatabase(List<String[]> rows) {
        for (String[] row : rows) {
            // process each record
            String[] values = row[0].split(";");
            String startAirportCode = values[0];
            String destAirportCode = values[1];
            int departureTime = Integer.parseInt(values[2]);
            int durationTime = Integer.parseInt(values[3]);
            try {
                Optional<Flight> existingFlight = flightRepository.getExistingFlight(durationTime, departureTime, destAirportCode, startAirportCode);
                if (existingFlight.isEmpty()) {
                    try {
                        addFlight(startAirportCode, destAirportCode, departureTime, durationTime);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            System.out.println(Arrays.toString(row));
        }
    }

    public void addFlight(String startCode, String destCode, int departureTime, int duration) {
        Airport startAirport = airportRepository.findByCode(startCode)
                .orElseThrow(() -> new IllegalArgumentException("Start airport not found"));
        Airport destAirport = airportRepository.findByCode(destCode)
                .orElseThrow(() -> new IllegalArgumentException("Destination airport not found"));
        Flight flight = Flight.builder().startAirport(startAirport).destAirport(destAirport).departureTime(departureTime).duration(duration).build();
        flightRepository.save(flight);
    }

    public String getAllFlightsFromAirport(String code) throws JsonProcessingException {
        Airport airport = airportRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found"));
        List<Flight> flights = flightRepository.findByCodeOrderByDestAirportCodeAscDepartureTimeAsc(code);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("code", code);

        Map<String, List<Flight>> flightsMap = new LinkedHashMap<>();
        flightsMap.put("departingFlights", airport.getDepartingFlights());
        flightsMap.put("arrivingFlights", airport.getArrivingFlights());
        flightsMap.put("flights", flights);

        responseMap.put("flights", flightsMap);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(responseMap);
    }


    public List<Flight> getAllDirectFlights(String startCode, String destCode) {
        return flightRepository.findByStartAirportCodeAndDestAirportCode(startCode, destCode);
    }

    public List<Flight> getAllDirectFlightsToAirport(String destCode) {
        return flightRepository.findByDestAirportCode(destCode);
    }

    public void deleteInactiveFlights(List<Flight> flights, List<String[]> rows) {
        for (Flight flight : flights) {
            boolean isActive = rows.stream().anyMatch(row -> {
                String[] splitRows = row[0].split(";");
                String startCode = splitRows[0];
                String destCode = splitRows[1];
                int duration = Integer.parseInt(splitRows[3]);
                int departureTime = Integer.parseInt(splitRows[2]);
                return startCode.equals(flight.getStartAirport().getCode())
                        && destCode.equals(flight.getDestAirport().getCode())
                        && duration == flight.getDuration()
                        && departureTime == flight.getDepartureTime();

            });
            System.out.println(isActive);
            if (!isActive) {
                flightRepository.delete(flight);
            }
        }
    }

    public void updateFlight(Long id, Flight updatedFlight) throws ResourceNotFoundException {
        Optional<Flight> flight = flightRepository.findById(id);

        if (flight.isEmpty()) {
            throw new ResourceNotFoundException("Flight not found with id " + id);
        }

        Flight existingFlight = flight.get();

        Airport departureAirport = airportRepository.findById(updatedFlight.getDestAirport().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination airport not found with id " + updatedFlight.getDestAirport().getId()));

        Airport arrivalAirport = airportRepository.findById(updatedFlight.getStartAirport().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Starting airport not found with id " + updatedFlight.getStartAirport().getId()));

        existingFlight.setDuration(updatedFlight.getDuration());
        existingFlight.setDepartureTime(updatedFlight.getDepartureTime());
        existingFlight.setDestAirport(departureAirport);
        existingFlight.setStartAirport(arrivalAirport);

        flightRepository.save(existingFlight);
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void deleteLongFlights() {
        flightRepository.deleteLongFlights(600); // 10 hours = 600 minutes
    }

    public List<Flight> getDynamicFlights(Map<String, String> requestParams) {
        // Build dynamic query based on request params
        String startingAirportCode = requestParams.get("startingAirportCode");
        String destinationAirportCode = requestParams.get("destinationAirportCode");
        Integer departureTime = Integer.parseInt(requestParams.get("departureTime"));
        Integer flightDuration = Integer.parseInt(requestParams.get("duration"));

        // Fetch Airport entities based on their codes

        Airport startingAirport = airportRepository.findByAirportCode(startingAirportCode);
        Airport destinationAirport = airportRepository.findByAirportCode(destinationAirportCode);

        // Create JPA criteria query to retrieve flights based on request params
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Flight> query = builder.createQuery(Flight.class);
        Root<Flight> root = query.from(Flight.class);

        List<Predicate> predicates = new ArrayList<>();
        if (startingAirport != null) {
            predicates.add(builder.equal(root.get("startAirport"), startingAirport));
        }
        if (destinationAirport != null) {
            predicates.add(builder.equal(root.get("destAirport"), destinationAirport));
        }
        predicates.add(builder.greaterThanOrEqualTo(root.get("departureTime"), departureTime));
        predicates.add(builder.lessThanOrEqualTo(root.get("flightDuration"), flightDuration));

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }
}

