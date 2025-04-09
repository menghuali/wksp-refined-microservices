package aloha.spring.microservices.booking_service.model;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Guest {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;

}
