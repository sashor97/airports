package com.airports.airports.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "airport")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int numPassengers;

    @JsonIgnore
    @OneToMany(mappedBy = "startAirport", cascade = CascadeType.REMOVE)
    private List<Flight> departingFlights;

    @JsonIgnore
    @OneToMany(mappedBy = "destAirport", cascade = CascadeType.REMOVE)
    private List<Flight> arrivingFlights;

    // constructors, getters and setters
}