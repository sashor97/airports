package com.airports.airports.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "flight")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "start_airport_id",nullable = false)
    private Airport startAirport;

    @ManyToOne
    @JoinColumn(name = "dest_airport_id",nullable = false)
    private Airport destAirport;

    @Column(nullable = false)
    private int departureTime;

    @Column(nullable = false)
    private int duration;

}