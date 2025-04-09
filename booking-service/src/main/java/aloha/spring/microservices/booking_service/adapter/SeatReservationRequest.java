package aloha.spring.microservices.booking_service.adapter;

import aloha.spring.microservices.booking_service.model.Flight;
import lombok.Value;

@Value
public class SeatReservationRequest {

    private Flight flight;
    private Integer seatAmount;

}
