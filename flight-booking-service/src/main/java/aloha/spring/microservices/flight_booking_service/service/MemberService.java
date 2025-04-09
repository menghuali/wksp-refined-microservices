package aloha.spring.microservices.flight_booking_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import aloha.spring.microservices.flight_booking_service.model.Membership;

@FeignClient(name = "membership-service")
public interface MemberService {

    @GetMapping("/memberships/{membership_number}")
    Membership getMembership(@PathVariable(name = "membership_number") Long membershipNumber);

}
