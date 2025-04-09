package aloha.spring.microservices.flight_booking_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aloha.spring.microservices.flight_booking_service.model.Booking;
import aloha.spring.microservices.flight_booking_service.model.Membership;
import aloha.spring.microservices.flight_booking_service.service.BookingService;
import aloha.spring.microservices.flight_booking_service.service.MemberService;

@RequestMapping(path = "/flight-bookings")
@RestController
public class FlightBookingController {
    private BookingService bookingSvc;
    private MemberService membershipSvc;

    @GetMapping("/{membership_number}")
    public List<Booking> getBookings(@PathVariable(name = "membership_number") Long membershipNumber) {
        Membership membership = membershipSvc.getMembership(membershipNumber);
        return bookingSvc.getBookings(membership.getNumber());
    }

    @PostMapping("/{membership_number}/book-flight")
    public Booking postMethodName(@PathVariable(name = "membership_number") Long membershipNumber,
            @RequestBody Booking booking) {
        Membership membership = membershipSvc.getMembership(membershipNumber);
        return bookingSvc.bookFlights(membership.getNumber(), booking);
    }

}
