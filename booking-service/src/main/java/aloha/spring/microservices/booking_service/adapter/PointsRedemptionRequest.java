package aloha.spring.microservices.booking_service.adapter;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class PointsRedemptionRequest {

    private Long loyaltyId;
    private BigDecimal totalPrice;

}
