package aloha.spring.microservices.booking_service.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import aloha.spring.microservices.booking_service.model.Booking;
import aloha.spring.microservices.booking_service.model.Flight;
import aloha.spring.microservices.booking_service.model.Guest;

@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
@DataJpaTest
public class BookingRepoTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepo bookingRepo;

    @Test
    public void testFindByFirstNameAndLastName() {
        Booking booking1 = Booking.builder().firstName("Peter").lastName("Parker").build();
        Booking booking2 = Booking.builder().firstName("Bruce").lastName("Wayne").build();
        em.persist(booking1);
        em.persist(booking2);
        List<Booking> bookings = bookingRepo.findByFirstNameAndLastName("Peter", "Parker");
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    public void testFindByLoyaltyId() {
        Booking booking1 = Booking.builder().loyaltyId(1l).build();
        Booking booking2 = Booking.builder().loyaltyId(2l).build();
        em.persist(booking1);
        em.persist(booking2);
        List<Booking> bookings = bookingRepo.findByLoyaltyId(1l);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    public void testCreatBooking() {
        Flight flight = Flight.builder().carrier("XX").number("123").origin("ABC").destination("DEF")
                .departureDateTime(LocalDateTime.now()).build();
        flight.initFlightId();
        Booking booking = Booking.builder().firstName("Peter").lastName("Parker").flight(flight)
                .guests(List.of(Guest.builder().firstName("Peter").lastName("Parker").build())).build();
        booking = bookingRepo.save(booking);
        assertNotNull(booking.getId());
        Optional<Booking> booking2 = bookingRepo.findById(booking.getId());
        assertTrue(booking2.isPresent());
        assertEquals(booking, booking2.get());
        assertEquals(flight, booking2.get().getFlight());
    }

    @Test
    public void testSaveSameFlightBookings() {
        Flight flight = Flight.builder().carrier("XX").number("123").origin("ABC").destination("DEF")
                .departureDateTime(LocalDateTime.now()).build();
        flight.initFlightId();

        Booking booking1 = Booking.builder().firstName("Peter").lastName("Parker").flight(flight)
                .guests(List.of(Guest.builder().firstName("Peter").lastName("Parker").build())).build();
        bookingRepo.save(booking1);

        Booking booking2 = Booking.builder().firstName("Bruce").lastName("Wayne").flight(flight)
                .guests(List.of(Guest.builder().firstName("Bruce").lastName("Wayne").build())).build();
        bookingRepo.save(booking2);
    }

}
