package aloha.spring.microservices.booking_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aloha.spring.microservices.booking_service.model.Booking;
import aloha.spring.microservices.booking_service.model.BookingRequest;
import aloha.spring.microservices.booking_service.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
@RestController
public class BookingController {

    private BookingService bookingSvc;

    @GetMapping("/{id}")
    public Booking getBooking(@PathVariable Long id) {
        Booking booking = bookingSvc.getBooking(id);
        return booking;
    }

    @GetMapping(path = "/name")
    public List<Booking> findByName(@RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName) {
        return bookingSvc.findBookings(firstName, lastName);
    }

    @GetMapping("/loyalty/{loyalty_id}")
    public List<Booking> findByLoyaltyId(@PathVariable(name = "loyalty_id") Long loyaltyId) {
        return bookingSvc.findBookings(loyaltyId);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public Booking bookFlight(@RequestBody BookingRequest request) {
        return bookingSvc.bookFlight(request);
    }

    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingSvc.cancelBooking(id);
    }

}
