package aloha.spring.microservices.flight_booking_service.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Flight {

    private String origin;

    private String destination;

    private LocalDateTime departure;

    private LocalDateTime arrival;

}
