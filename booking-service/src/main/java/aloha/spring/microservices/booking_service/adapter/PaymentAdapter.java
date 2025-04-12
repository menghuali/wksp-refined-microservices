package aloha.spring.microservices.booking_service.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/payments")
@FeignClient("payment-service")
public interface PaymentAdapter {

    @PutMapping("/{id}/refund")
    void refund(@PathVariable(name = "id") Long paymentId);

    @PostMapping
    Payment pay(PaymentRequest paymentRequest);

}
