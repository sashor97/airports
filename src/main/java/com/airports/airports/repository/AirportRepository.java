package com.airports.airports.repository;

import com.airports.airports.models.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByCode(String code);

    @Modifying
    @Query("update Airport a set a.name = ?1, a.country = ?2 , a.numPassengers = ?3 where a.code = ?4")
    void updateAirport(String name, String country,int numPassengers,String airportCode);

    @Transactional(readOnly = true)
    @Query("SELECT a FROM Airport as a WHERE a.code = :airportCode")
    public Airport findByAirportCode(@Param("airportCode") String airportCode);

    @Transactional(readOnly = true)
    @Query("SELECT a FROM Airport as a WHERE a.code IN :airportCodes")
    public List<Airport> findByAirportCodesIn(@Param("airportCodes") List<String> airportCodes);



}
