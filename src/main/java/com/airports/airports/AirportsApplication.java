package com.airports.airports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class AirportsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirportsApplication.class, args);
	}

}
