package aloha.spring.microservices.booking_service.service;

import static aloha.spring.microservices.booking_service.model.BookingStatus.BOOKED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aloha.spring.microservices.booking_service.adapter.FlightInventoryAdapter;
import aloha.spring.microservices.booking_service.adapter.LoyaltyAdapter;
import aloha.spring.microservices.booking_service.adapter.PaymentAdapter;
import aloha.spring.microservices.booking_service.model.Booking;
import aloha.spring.microservices.booking_service.repo.BookingRepo;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTests {

    @Mock
    private BookingRepo repo;

    @Mock
    private FlightInventoryAdapter fiAdapter;

    @Mock
    private LoyaltyAdapter loyaltyAdapter;

    @Mock
    private PaymentAdapter paymentAdapter;

    @InjectMocks
    private BookingServiceImpl svc;

    @Test
    public void testGetBooking() {
        Booking booking = Booking.builder().id(1l).status(BOOKED).build();
        when(repo.findById(1l)).thenReturn(Optional.of(booking));
        assertEquals(booking, svc.getBooking(1l));
    }

    @Test
    public void testGetBooking_NotFound() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> svc.getBooking(1l));
    }

}
