package aloha.spring.microservices.flight_inventory_service.service;

import java.time.LocalDateTime;
import java.util.List;

import aloha.spring.microservices.flight_inventory_service.model.FlightInventory;
import aloha.spring.microservices.flight_inventory_service.model.Reservation;

public interface FlightInventoryService {

    Reservation reserveSeats(Long flightId, Long bookingId, Integer seatAmount);

    void cancelReservation(Long id);

    Reservation getReservation(Long id);

    List<FlightInventory> searchFlights(String origin, String destination, LocalDateTime departureDateTime);

    FlightInventory createFlightInventory(FlightInventory inventory);

}
