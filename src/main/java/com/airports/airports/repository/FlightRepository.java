package com.airports.airports.repository;

import com.airports.airports.models.Airport;
import com.airports.airports.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM  Flight f  WHERE f.destAirport.code = :code ORDER BY f.departureTime ASC")
    List<Flight> findByCodeOrderByDestAirportCodeAscDepartureTimeAsc(String code);

    @Query("SELECT f FROM Flight f  WHERE f.startAirport.code = :startCode and f.destAirport.code = :destCode")
    List<Flight> findByStartAirportCodeAndDestAirportCode(String startCode, String destCode);

    @Query("SELECT f FROM  Flight f  WHERE f.destAirport.code = :destCode")
    List<Flight> findByDestAirportCode(String destCode);
    @Modifying
    @Query("DELETE FROM Flight f WHERE f.duration > :maxDuration")
    void deleteLongFlights(@Param("maxDuration") int maxDuration);

    @Query("select f from Flight f where f.departureTime =:departureTime and f.duration =:duration and f.destAirport.code =:destCode and f.startAirport.code = :startCode")
    Optional<Flight> getExistingFlight(@Param("duration") int duration, @Param("departureTime") int departureTime, @Param("destCode") String destCode, @Param("startCode") String startCode);
    @Modifying
    @Query("update Flight f set f.departureTime = ?1, f.duration = ?2 , f.destAirport.code = ?3,f.startAirport.code = ?4")
    void updateAirport( int departureTime,int duration,String destCode,String startCode);


}
