package aloha.spring.microservices.booking_service.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Flight {

    private String carrier;
    private String number;
    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;

}
