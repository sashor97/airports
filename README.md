# Airports managing system
This project is an API for managing airports and flights, built using Spring Boot and JPA. It provides endpoints for adding new airports and flights, retrieving flights from a specific airport, and more. You can also build a docker image for this application by click RUN on `Dockerfile`. You can also run it on kubernetes by applying files in `kubernetes` folder

# Getting started
To get started with this project, clone the repository and install the necessary dependencies. You will also need to set up a MySQL database and update the application.properties file with your database credentials.
## Technologies
The following technologies have been used to build this system:

- Spring Boot
- MySQL
- Hibernate
- Maven
- Docker
- Kubernetes


## Functionality
The system provides the following functionality:
- Reading csv files and storing into database
- Add new airport to the system.
- Add new flight to the system.
- Show all flights from an airport with a specific code.
- Show all direct flights from one airport to another.
- Show all direct flights to a specific airport.
- Return the airport with most passengers during one year for a specific country.
- Delete a specific airport.
- Delete flights with duration greater than 10 hours.
- Retrieve flights based on various criteria specified by query parameters.

## Getting Started
To get started with this project, clone the repository and install the necessary dependencies. You will also need to set up a MySQL database and update the application.properties file with your database credentials.
```
git clone https://github.com/sashor97/airports.git
cd airports
mvn install
```
To run the application, use the following command:

```
mvn spring-boot:run

```
The application will be available at `http://localhost:8080.`


## Usage
Once the system is running, you can use the following endpoints to interact with the system:

# AirportController
This controller handles HTTP requests related to airports. It includes the following methods:

`POST /airports/read-csv`: uploads airports data from a CSV file.
`POST /airports`: adds a new airport to the system.
`GET /airports/{country}/mostPassengers`: retrieves the airport with the most passengers for a given country.
`DELETE /airports/{code}`: deletes an airport from the system given its code.

# FlightController
This is a Spring Boot REST controller that handles flight-related requests. It provides endpoints to read flight data from a CSV file, retrieve flights based on various criteria such as airport code, origin and destination, and update existing flights.

Endpoints
The following endpoints are available:

`POST /flights/read-csv`: This endpoint reads flight data from a CSV file and saves it in the application's data store.

`GET /flights/{code}/all`: This endpoint retrieves all flights departing from the specified airport, where {code} is the airport code.

`GET /flights/direct`: This endpoint retrieves all direct flights between two airports, specified by the "start" and "dest" query parameters.

`GET /flights/direct-to-destination`: This endpoint retrieves all direct flights to a specific destination airport, specified by the "dest" query parameter.

`PUT /flights/{id}`: This endpoint updates an existing flight with the specified ID, where {id} is the flight ID. The updated flight data is sent in the request body.

`GET /flights`: This endpoint retrieves flights based on various criteria specified by query parameters.

### Dependencies
This controller depends on the FlightService class for flight-related business logic and the ResourceLoader class for loading the CSV file.

### Error handling
This controller handles the ResourceNotFoundException exception that can be thrown when a flight or a resource is not found. It returns an appropriate error response to the client.

# Tests
This project contains tests for both the Airport and Flight controllers. The tests use JUnit 5 and the Spring Boot Test framework to test the various endpoints exposed by the controllers.

## Conclusion
This Airports managing system provides a simple and easy way to manage and query airports and flights data. By following the steps outlined in this README, you can easily set up and use the system for your own purposes.
