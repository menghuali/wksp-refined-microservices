package aloha.spring.microservices.booking_service.adapter;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Payment {

    private Long id;
    private BigDecimal amountPaid;

    @JsonCreator
    public Payment(@JsonProperty("id") Long id, @JsonProperty("amountPaid") BigDecimal amountPaid) {
        this.id = id;
        this.amountPaid = amountPaid;
    }

}
