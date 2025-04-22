package aloha.spring.microservices.flight_inventory_service.controller;

import lombok.Value;

@Value
public class ReservationRequest {

    private Long bookingId;
    private Integer seatAmount;

}
