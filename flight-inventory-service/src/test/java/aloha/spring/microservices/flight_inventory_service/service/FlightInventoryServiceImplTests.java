package aloha.spring.microservices.flight_inventory_service.service;

import static aloha.spring.microservices.flight_inventory_service.model.ReservationStatus.ACTIVE;
import static aloha.spring.microservices.flight_inventory_service.model.ReservationStatus.CANCELLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aloha.spring.microservices.flight_inventory_service.model.FlightInventory;
import aloha.spring.microservices.flight_inventory_service.model.Reservation;
import aloha.spring.microservices.flight_inventory_service.repo.FlightInventoryRepo;
import aloha.spring.microservices.flight_inventory_service.repo.ReservationRepo;

@ExtendWith(MockitoExtension.class)
public class FlightInventoryServiceImplTests {

    @Mock
    private FlightInventoryRepo fiRepo;

    @Mock
    private ReservationRepo rsRepo;

    @InjectMocks
    private FlightInventoryServiceImpl svc;

    @Test
    public void testReserveSeats_FlightNotFound() {
        when(fiRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(FlightNotFoundException.class, () -> svc.reserveSeats(1l, 2l, 3));
    }

    @Test
    public void testReserveSeats_NoEnoughSeat() {
        Long flightId = 1l;
        when(fiRepo.findById(flightId))
                .thenReturn(Optional.of(FlightInventory.builder().capacity(10).seatsBooked(8).build()));
        assertThrows(NoEnoughSeatException.class, () -> svc.reserveSeats(flightId, 2l, 3));
    }

    @Test
    public void testReserveSeats() {
        Long flightId = 1l, bookingId = 2l, reservationId = 3l;
        Integer seatAmount = 3;
        FlightInventory inventory = FlightInventory.builder().id(flightId).capacity(10).seatsBooked(7)
                .reservations(new ArrayList<>()).unitPrice(BigDecimal.valueOf(100)).build();
        when(fiRepo.findById(flightId)).thenReturn(Optional.of(inventory));

        when(rsRepo.save(notNull(Reservation.class))).thenAnswer(invocation -> {
            Reservation rs = invocation.getArgument(0, Reservation.class);
            rs.setId(reservationId);
            return rs;
        });

        Reservation reservation = svc.reserveSeats(flightId, bookingId, seatAmount);
        verify(rsRepo, times(1)).save(reservation);
        assertEquals(reservationId, reservation.getId());
        assertEquals(bookingId, reservation.getBookingId());
        assertEquals(seatAmount, reservation.getSeatAmount());
        assertEquals(inventory, reservation.getFlightInventory());
        assertEquals(BigDecimal.valueOf(300), reservation.getAmountDue());
        assertEquals(ACTIVE, reservation.getStatus());

        verify(fiRepo, times(1)).save(inventory);
        assertEquals(1, inventory.getReservations().size());
        assertEquals(reservation, inventory.getReservations().get(0));
        assertEquals(10, inventory.getSeatsBooked());
    }

    @Test
    public void testCancelReservation() {
        Long flightId = 1l, bookingId = 2l, reservationId = 3l;
        Integer seatAmount = 1, seatsBooked = 4;

        FlightInventory inventory = FlightInventory.builder().id(flightId).seatsBooked(seatsBooked)
                .reservations(new ArrayList<>()).build();
        Reservation reservation = Reservation.builder().id(reservationId).bookingId(bookingId)
                .flightInventory(inventory).seatAmount(seatAmount).status(ACTIVE).build();
        inventory.getReservations().add(reservation);

        when(rsRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        svc.cancelReservation(reservationId);

        verify(rsRepo, times(1)).save(reservation);
        assertEquals(CANCELLED, reservation.getStatus());

        verify(fiRepo, times(1)).save(inventory);
        assertEquals(seatsBooked - seatAmount, inventory.getSeatsBooked());
    }

    @Test
    public void testGetReservation_NotFound() {
        when(rsRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ReservationNotFoundException.class, () -> svc.getReservation(1l));
    }

    @Test
    public void testGetReservation() {
        Long id = 1l;
        Reservation reservation = Reservation.builder().id(id).build();
        when(rsRepo.findById(id)).thenReturn(Optional.of(reservation));
        assertEquals(reservation, svc.getReservation(id));
    }

    @Test
    public void testSearchFlights() {
        String origin = "AAA", destination = "BBB";
        LocalDateTime departureDateTime = LocalDateTime.of(2025, 5, 1, 10, 30, 0);
        when(fiRepo.findByOriginAndDestinationAndDepartureDateTime(origin, destination, departureDateTime)).thenReturn(
                List.of(FlightInventory.builder().id(1l).build(), FlightInventory.builder().id(2l).build()));
        List<FlightInventory> flights = svc.searchFlights(origin, destination, departureDateTime);
        assertNotNull(flights);
        assertEquals(2, flights.size());
        assertNotNull(flights.get(0));
        assertEquals(1l, flights.get(0).getId());
        assertNotNull(flights.get(1));
        assertEquals(2l, flights.get(1).getId());
    }

    @Test
    public void testCreateFlight() {
        FlightInventory inventory = FlightInventory.builder().id(1l).build();
        svc.createFlightInventory(inventory);
        verify(fiRepo, times(1)).save(inventory);
    }

}
