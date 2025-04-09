package aloha.spring.microservices.flight_booking_service.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Membership {

    private Long number;

    private String firtName;

    private String lastName;

    private Gender gener;

    private LocalDate dateOfBirth;

}
