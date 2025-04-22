package aloha.spring.microservices.flight_inventory_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aloha.spring.microservices.flight_inventory_service.model.FlightInventory;
import aloha.spring.microservices.flight_inventory_service.model.Reservation;
import aloha.spring.microservices.flight_inventory_service.service.FlightInventoryService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class FlightInventoryController {

    private FlightInventoryService svc;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(path = "/flight-inventories/{id}/reservations")
    public Reservation reserveSeats(@PathVariable(name = "id", required = true) Long flightId,
            @RequestBody ReservationRequest request) {
        return svc.reserveSeats(flightId, request.getBookingId(), request.getSeatAmount());
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PutMapping(path = "/reservations/{id}/cancel")
    public void cancelReservation(@PathVariable Long id) {
        svc.cancelReservation(id);
    }

    @GetMapping("/reservations/{id}")
    public Reservation getReservation(@PathVariable Long id) {
        return svc.getReservation(id);
    }

    @GetMapping("/flight-inventories/search")
    public List<FlightInventory> searchFlights(@RequestParam(required = true) String origin,
            @RequestParam(required = true) String destination,
            @RequestParam(required = true) LocalDateTime departureDateTime) {
        return svc.searchFlights(origin, destination, departureDateTime);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/flight-inventories")
    public FlightInventory createFlightInventory(@RequestBody FlightInventory inventory) {
        return svc.createFlightInventory(inventory);
    }

}
