package aloha.spring.microservices.flight_booking_service.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Booking {

    private String referenceNumber;

    private Long eTicketNumber;

    private String firtName;

    private String lastName;

    private String membershipNumber;

    private List<Flight> itinerary;

}
