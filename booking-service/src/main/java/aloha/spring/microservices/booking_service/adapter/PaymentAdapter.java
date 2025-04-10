package aloha.spring.microservices.booking_service.adapter;

public interface PaymentAdapter {

    void refund(Long paymentId);

    Payment pay(PaymentRequest paymentRequest);

}
