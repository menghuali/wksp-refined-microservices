package aloha.spring.microservices.flight_inventory_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import aloha.spring.microservices.flight_inventory_service.model.FlightInventory;
import aloha.spring.microservices.flight_inventory_service.model.Reservation;
import aloha.spring.microservices.flight_inventory_service.model.ReservationStatus;
import aloha.spring.microservices.flight_inventory_service.repo.FlightInventoryRepo;
import aloha.spring.microservices.flight_inventory_service.repo.ReservationRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class FlightInventoryServiceImpl implements FlightInventoryService {

    private FlightInventoryRepo fiRepo;

    private ReservationRepo rsRepo;

    @Transactional
    @Override
    public Reservation reserveSeats(Long flightId, Long bookingId, Integer seatAmount) {
        Optional<FlightInventory> opt = fiRepo.findById(flightId);
        if (opt.isEmpty()) {
            throw new FlightNotFoundException();
        }
        FlightInventory inventory = opt.get();
        if (inventory.getCapacity() - inventory.getSeatsBooked() < seatAmount) {
            throw new NoEnoughSeatException();
        }
        Reservation reservation = Reservation.builder()
                .flightInventory(inventory)
                .bookingId(bookingId)
                .seatAmount(seatAmount)
                .amountDue(inventory.getUnitPrice().multiply(BigDecimal.valueOf(seatAmount)))
                .status(ReservationStatus.ACTIVE)
                .build();
        inventory.reserveSeat(seatAmount);
        rsRepo.save(reservation);
        fiRepo.save(inventory);
        return reservation;
    }

    @Transactional
    @Override
    public void cancelReservation(Long id) {
        Reservation reservation = getReservation(id);
        FlightInventory inventory = reservation.getFlightInventory();
        inventory.cancelReservation(reservation.getSeatAmount());
        reservation.setStatus(ReservationStatus.CANCELLED);
        rsRepo.save(reservation);
        fiRepo.save(inventory);
    }

    @Override
    public Reservation getReservation(Long id) {
        Optional<Reservation> opt = rsRepo.findById(id);
        if (opt.isEmpty()) {
            throw new ReservationNotFoundException();
        }
        return opt.get();
    }

    @Override
    public List<FlightInventory> searchFlights(String origin, String destination, LocalDateTime departureDateTime) {
        return fiRepo.findByOriginAndDestinationAndDepartureDateTime(origin, destination, departureDateTime);
    }

    @Override
    public FlightInventory createFlightInventory(FlightInventory inventory) {
        FlightInventory saved = fiRepo.save(inventory);
        return saved;
    }

}
