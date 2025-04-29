package aloha.spring.microservices.flight_inventory_service.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class FlightInventoryTests {

    @Test
    public void testReserveSeat() {
        FlightInventory inventory = FlightInventory.builder().reservations(new ArrayList<>()).capacity(10)
                .seatsBooked(7).build();
        Reservation reservation = Reservation.builder().seatAmount(2).build();
        inventory.reserveSeat(reservation);
        assertEquals(9, inventory.getSeatsBooked());
        assertEquals(1, inventory.getReservations().size());
        assertEquals(reservation, inventory.getReservations().get(0));
    }

    @Test
    public void testCancelReservation() {
        FlightInventory inventory = FlightInventory.builder().reservations(new ArrayList<>()).capacity(10)
                .seatsBooked(7).build();
        Reservation reservation = Reservation.builder().seatAmount(2).build();
        inventory.getReservations().add(reservation);
        inventory.cancelReservation(reservation);
        assertEquals(5, inventory.getSeatsBooked());
        assertEquals(1, inventory.getReservations().size());
        assertEquals(reservation, inventory.getReservations().get(0));
    }

}
