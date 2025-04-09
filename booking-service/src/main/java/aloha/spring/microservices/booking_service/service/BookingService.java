package aloha.spring.microservices.booking_service.service;

import java.util.List;

import aloha.spring.microservices.booking_service.model.Booking;
import aloha.spring.microservices.booking_service.model.BookingRequest;

public interface BookingService {

    Booking getBooking(Long id);

    List<Booking> findBookings(String firstName, String lastName);

    List<Booking> findBookings(Long loyaltyID);

    Booking bookFlight(BookingRequest request);

    Booking cancelBooking(Long id);

}
