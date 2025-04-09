package aloha.spring.microservices.booking_service.adapter;

import java.math.BigDecimal;

public interface PaymentAdapter {

    Long pay(BigDecimal amountDue);

    void refund(Long paymentID);

    Payment pay(PaymentRequest paymentRequest);

}
