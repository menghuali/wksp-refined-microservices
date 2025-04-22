package aloha.spring.microservices.flight_inventory_service.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Reservation {

    @GeneratedValue
    private Long id;
    private Long bookingId;
    private Integer seatAmount;
    private BigDecimal amountDue;
    private ReservationStatus status;

    @JsonIgnore
    @JoinColumn(name = "flight_inventory_id")
    @ManyToOne
    private FlightInventory flightInventory;

}
