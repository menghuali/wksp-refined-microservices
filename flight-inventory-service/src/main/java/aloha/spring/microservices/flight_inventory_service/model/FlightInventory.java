package aloha.spring.microservices.flight_inventory_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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

    @GeneratedValue
    private Long id;

    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;
    private Integer capacity;
    private Integer seatsBooked;
    private BigDecimal unitPrice;

    public void reserveSeat(Integer seatAmount) {
        seatsBooked += seatAmount;
    }

    public void cancelReservation(Integer seatAmount) {
        seatsBooked -= seatAmount;
    }

}
