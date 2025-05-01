package aloha.spring.microservices.flight_inventory_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class FlightInventory {

    @Id
    @GeneratedValue
    private Long id;

    private String flightNumber;
    private String origin;
    private String destination;

    @Column(name = "DEPARTURE")
    private LocalDateTime departureDateTime;
    private Integer capacity;
    private Integer seatsBooked;
    private BigDecimal unitPrice;

    @OneToMany(mappedBy = "flightInventory")
    private List<Reservation> reservations;

    public void reserveSeat(Reservation reservation) {
        reservations.add(reservation);
        seatsBooked += reservation.getSeatAmount();
    }

    public void cancelReservation(Reservation reservation) {
        seatsBooked -= reservation.getSeatAmount();
    }

}
