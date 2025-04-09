package aloha.spring.microservices.flight_booking_service.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import aloha.spring.microservices.flight_booking_service.model.Booking;

@FeignClient(name = "booking-backend")
public interface BookingService {

    @GetMapping(path = "/bookings/{membership_number}")
    List<Booking> getBookings(@PathVariable(name = "membership_number") Long membershipNumber);

    @PostMapping(path = "/bookings/{membership_number}/book-flight")
    Booking bookFlights(@PathVariable(name = "membership_number") Long membershipNumber, @RequestBody Booking booking);

}
