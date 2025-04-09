package aloha.spring.microservices.booking_service.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingRequest {

    private Flight flight;
    private String firstName;
    private String lastName;
    private List<Guest> guests;
    private String crediCardNumber;
    private Long loyaltyId;
    private Boolean redeemingPoints;

}
