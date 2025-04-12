package aloha.spring.microservices.booking_service.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Guest {

    @GeneratedValue
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

}
