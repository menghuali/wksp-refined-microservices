package aloha.spring.microservices.booking_service.adapter;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class PointsEarningRequest {

    private Long loyaltyId;
    private BigDecimal amountPaid;

}
