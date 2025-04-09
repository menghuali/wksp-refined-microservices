package aloha.spring.microservices.booking_service.adapter;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class PaymentRequest {

    private BigDecimal amountToPay;
    private String creditCardNumber;

}
