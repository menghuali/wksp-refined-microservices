package aloha.spring.microservices.booking_service.adapter;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class SeatReservation {

    private Long id;

    private BigDecimal amountDue;

    @JsonCreator
    public SeatReservation(@JsonProperty("id") Long id, @JsonProperty("amountDue") BigDecimal amountDue) {
        this.id = id;
        this.amountDue = amountDue;
    }

}
