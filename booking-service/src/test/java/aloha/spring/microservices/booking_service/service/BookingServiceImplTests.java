package aloha.spring.microservices.booking_service.service;

import static aloha.spring.microservices.booking_service.model.BookingStatus.BOOKED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aloha.spring.microservices.booking_service.adapter.FlightInventoryAdapter;
import aloha.spring.microservices.booking_service.adapter.LoyaltyAdapter;
import aloha.spring.microservices.booking_service.adapter.Payment;
import aloha.spring.microservices.booking_service.adapter.PaymentAdapter;
import aloha.spring.microservices.booking_service.adapter.PointsEarned;
import aloha.spring.microservices.booking_service.adapter.PointsRedemption;
import aloha.spring.microservices.booking_service.adapter.SeatReservation;
import aloha.spring.microservices.booking_service.model.Booking;
import aloha.spring.microservices.booking_service.model.BookingRequest;
import aloha.spring.microservices.booking_service.model.Flight;
import aloha.spring.microservices.booking_service.model.Guest;
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

        @Test
        public void testFindBookings_StringString() {
                String firstName = "Peter", lastName = "Parker";
                Booking booking = Booking.builder().id(1l).firstName(firstName).lastName(lastName).build();
                when(repo.findByFirstNameAndLastName(firstName, lastName)).thenReturn(List.of(booking));
                List<Booking> bookings = svc.findBookings(firstName, lastName);
                assertEquals(1, bookings.size());
                assertEquals(booking, bookings.get(0));
        }

        @Test
        public void testFindBookings_Long() {
                Long loyaltyId = 1234l;
                Booking booking = Booking.builder().id(1l).build();
                when(repo.findByLoyaltyId(loyaltyId)).thenReturn(List.of(booking));
                List<Booking> bookings = svc.findBookings(loyaltyId);
                assertEquals(1, bookings.size());
                assertEquals(booking, bookings.get(0));
        }

        @Test
        public void testBookFlight() {
                Flight flight = Flight.builder().carrier("WS").number("1234").origin("ABC").destination("DEF")
                                .departureDateTime(LocalDateTime.now()).build();
                Guest guest1 = Guest.builder().firstName("Peter").lastName("Parker").build();
                Guest guest2 = Guest.builder().firstName("Bruce").lastName("Wayne").build();
                String creditCardNumber = "5555 5555 5555 4444";
                Long loyaltyId = 123456789l;

                BookingRequest request = BookingRequest.builder()
                                .flight(flight)
                                .firstName("Peter").lastName("Parker")
                                .guests(List.of(guest1, guest2))
                                .crediCardNumber(creditCardNumber)
                                .loyaltyId(loyaltyId)
                                .redeemingPoints(true)
                                .build();

                Long seatReservationId = 123l;
                BigDecimal totalPrice = BigDecimal.valueOf(1000);
                when(fiAdapter.reserveSeat(
                                argThat(seatReserveReq -> seatReserveReq.getFlight() == flight
                                                && seatReserveReq.getSeatAmount() == 2)))
                                .thenReturn(new SeatReservation(seatReservationId, totalPrice));

                Long redemptionId = 321l;
                Boolean redeemed = true;
                Integer pointsRedeemed = 1000;
                BigDecimal deductionAmount = BigDecimal.valueOf(100);
                Integer pointsBalance = 100;
                when(loyaltyAdapter
                                .redeemPoints(argThat(pointsRedemptionRequest -> pointsRedemptionRequest
                                                .getLoyaltyId() == loyaltyId
                                                && pointsRedemptionRequest.getTotalPrice().equals(totalPrice))))
                                .thenReturn(
                                                new PointsRedemption(redemptionId, redeemed, pointsRedeemed,
                                                                deductionAmount, pointsBalance));

                Long paymentId = 123321l;
                BigDecimal amountToPay = totalPrice.subtract(deductionAmount);
                when(paymentAdapter.pay(argThat(paymentRequest -> paymentRequest.getAmountToPay().equals(amountToPay)
                                && paymentRequest.getCreditCardNumber().equals(creditCardNumber))))
                                .thenReturn(new Payment(paymentId, amountToPay));

                Long pointsEarningId = 456l;
                Integer pointsEarned = 20;
                when(loyaltyAdapter
                                .earnPoints(argThat(pointsEarningRequest -> pointsEarningRequest.getLoyaltyId()
                                                .equals(loyaltyId)
                                                && pointsEarningRequest.getAmountPaid().equals(amountToPay))))
                                .thenReturn(new PointsEarned(pointsEarningId, pointsEarned));

                Long bookingId = 789l;
                when(repo.save(any(Booking.class))).thenAnswer(invocation -> {
                        Booking booking = invocation.getArgument(0);
                        booking.setId(bookingId);
                        return booking;
                });

                Booking booking = svc.bookFlight(request);
                assertEquals(seatReservationId, booking.getSeatsReservationId());
                assertEquals(redemptionId, booking.getRedemptionId());
                assertEquals(pointsRedeemed, booking.getPointsRedeemed());
                assertEquals(deductionAmount, booking.getDeductionAmount());
                assertEquals(paymentId, booking.getPaymentId());
                assertEquals(amountToPay, booking.getAmountPaid());
                assertEquals(pointsEarningId, booking.getPointsEarningId());
                assertEquals(pointsEarned, booking.getPointsEarned());
                assertEquals(bookingId, booking.getId());
                assertEquals(BOOKED, booking.getStatus());
        }

        @Test
        public void testBookFlight_NoRedemption() {
                Flight flight = Flight.builder().carrier("WS").number("1234").origin("ABC").destination("DEF")
                                .departureDateTime(LocalDateTime.now()).build();
                Guest guest1 = Guest.builder().firstName("Peter").lastName("Parker").build();
                Guest guest2 = Guest.builder().firstName("Bruce").lastName("Wayne").build();
                String creditCardNumber = "5555 5555 5555 4444";
                Long loyaltyId = 123456789l;

                BookingRequest request = BookingRequest.builder()
                                .flight(flight)
                                .firstName("Peter").lastName("Parker")
                                .guests(List.of(guest1, guest2))
                                .crediCardNumber(creditCardNumber)
                                .loyaltyId(loyaltyId)
                                .redeemingPoints(false)
                                .build();

                Long seatReservationId = 123l;
                BigDecimal totalPrice = BigDecimal.valueOf(1000);
                when(fiAdapter.reserveSeat(
                                argThat(seatReserveReq -> seatReserveReq.getFlight() == flight
                                                && seatReserveReq.getSeatAmount() == 2)))
                                .thenReturn(new SeatReservation(seatReservationId, totalPrice));

                Long paymentId = 123321l;
                BigDecimal amountToPay = totalPrice;
                when(paymentAdapter.pay(argThat(paymentRequest -> paymentRequest.getAmountToPay().equals(amountToPay)
                                && paymentRequest.getCreditCardNumber().equals(creditCardNumber))))
                                .thenReturn(new Payment(paymentId, amountToPay));

                Long pointsEarningId = 456l;
                Integer pointsEarned = 20;
                when(loyaltyAdapter
                                .earnPoints(argThat(pointsEarningRequest -> pointsEarningRequest.getLoyaltyId()
                                                .equals(loyaltyId)
                                                && pointsEarningRequest.getAmountPaid().equals(amountToPay))))
                                .thenReturn(new PointsEarned(pointsEarningId, pointsEarned));

                Long bookingId = 789l;
                when(repo.save(any(Booking.class))).thenAnswer(invocation -> {
                        Booking booking = invocation.getArgument(0);
                        booking.setId(bookingId);
                        return booking;
                });

                Booking booking = svc.bookFlight(request);
                assertEquals(seatReservationId, booking.getSeatsReservationId());
                assertNull(booking.getRedemptionId());
                verify(loyaltyAdapter, never()).redeemPoints(any());
                assertEquals(0, booking.getPointsRedeemed());
                assertEquals(BigDecimal.ZERO, booking.getDeductionAmount());
                assertEquals(paymentId, booking.getPaymentId());
                assertEquals(amountToPay, booking.getAmountPaid());
                assertEquals(pointsEarningId, booking.getPointsEarningId());
                assertEquals(pointsEarned, booking.getPointsEarned());
                assertEquals(bookingId, booking.getId());
                assertEquals(BOOKED, booking.getStatus());
        }

        @Test
        public void testBookFlight_NoLoyaltyId() {
                Flight flight = Flight.builder().carrier("WS").number("1234").origin("ABC").destination("DEF")
                                .departureDateTime(LocalDateTime.now()).build();
                Guest guest1 = Guest.builder().firstName("Peter").lastName("Parker").build();
                Guest guest2 = Guest.builder().firstName("Bruce").lastName("Wayne").build();
                String creditCardNumber = "5555 5555 5555 4444";

                BookingRequest request = BookingRequest.builder()
                                .flight(flight)
                                .firstName("Peter").lastName("Parker")
                                .guests(List.of(guest1, guest2))
                                .crediCardNumber(creditCardNumber)
                                .redeemingPoints(true)
                                .build();

                Long seatReservationId = 123l;
                BigDecimal totalPrice = BigDecimal.valueOf(1000);
                when(fiAdapter.reserveSeat(
                                argThat(seatReserveReq -> seatReserveReq.getFlight() == flight
                                                && seatReserveReq.getSeatAmount() == 2)))
                                .thenReturn(new SeatReservation(seatReservationId, totalPrice));

                Long paymentId = 123321l;
                BigDecimal amountToPay = totalPrice;
                when(paymentAdapter.pay(argThat(paymentRequest -> paymentRequest.getAmountToPay().equals(amountToPay)
                                && paymentRequest.getCreditCardNumber().equals(creditCardNumber))))
                                .thenReturn(new Payment(paymentId, amountToPay));

                Long bookingId = 789l;
                when(repo.save(any(Booking.class))).thenAnswer(invocation -> {
                        Booking booking = invocation.getArgument(0);
                        booking.setId(bookingId);
                        return booking;
                });

                Booking booking = svc.bookFlight(request);
                assertEquals(seatReservationId, booking.getSeatsReservationId());
                verifyNoInteractions(loyaltyAdapter);
                assertNull(booking.getRedemptionId());
                assertEquals(0, booking.getPointsRedeemed());
                assertEquals(BigDecimal.ZERO, booking.getDeductionAmount());
                assertEquals(paymentId, booking.getPaymentId());
                assertEquals(amountToPay, booking.getAmountPaid());
                assertNull(booking.getPointsEarningId());
                assertEquals(0, booking.getPointsEarned());
                assertEquals(bookingId, booking.getId());
                assertEquals(BOOKED, booking.getStatus());
        }

        @Test
        public void testBookFlight_Rollback() {
                Flight flight = Flight.builder().carrier("WS").number("1234").origin("ABC").destination("DEF")
                                .departureDateTime(LocalDateTime.now()).build();
                Guest guest1 = Guest.builder().firstName("Peter").lastName("Parker").build();
                Guest guest2 = Guest.builder().firstName("Bruce").lastName("Wayne").build();
                String creditCardNumber = "5555 5555 5555 4444";
                Long loyaltyId = 123456789l;

                BookingRequest request = BookingRequest.builder()
                                .flight(flight)
                                .firstName("Peter").lastName("Parker")
                                .guests(List.of(guest1, guest2))
                                .crediCardNumber(creditCardNumber)
                                .loyaltyId(loyaltyId)
                                .redeemingPoints(true)
                                .build();

                Long seatReservationId = 123l;
                BigDecimal totalPrice = BigDecimal.valueOf(1000);
                when(fiAdapter.reserveSeat(
                                argThat(seatReserveReq -> seatReserveReq.getFlight() == flight
                                                && seatReserveReq.getSeatAmount() == 2)))
                                .thenReturn(new SeatReservation(seatReservationId, totalPrice));

                Long redemptionId = 321l;
                Boolean redeemed = true;
                Integer pointsRedeemed = 1000;
                BigDecimal deductionAmount = BigDecimal.valueOf(100);
                Integer pointsBalance = 100;
                when(loyaltyAdapter
                                .redeemPoints(argThat(pointsRedemptionRequest -> pointsRedemptionRequest
                                                .getLoyaltyId() == loyaltyId
                                                && pointsRedemptionRequest.getTotalPrice().equals(totalPrice))))
                                .thenReturn(
                                                new PointsRedemption(redemptionId, redeemed, pointsRedeemed,
                                                                deductionAmount, pointsBalance));

                Long paymentId = 123321l;
                BigDecimal amountToPay = totalPrice.subtract(deductionAmount);
                when(paymentAdapter.pay(argThat(paymentRequest -> paymentRequest.getAmountToPay().equals(amountToPay)
                                && paymentRequest.getCreditCardNumber().equals(creditCardNumber))))
                                .thenReturn(new Payment(paymentId, amountToPay));

                Long pointsEarningId = 456l;
                Integer pointsEarned = 20;
                when(loyaltyAdapter
                                .earnPoints(argThat(pointsEarningRequest -> pointsEarningRequest.getLoyaltyId()
                                                .equals(loyaltyId)
                                                && pointsEarningRequest.getAmountPaid().equals(amountToPay))))
                                .thenReturn(new PointsEarned(pointsEarningId, pointsEarned));

                when(repo.save(any(Booking.class))).thenThrow(new RuntimeException());

                Exception ex = null;
                try {
                        svc.bookFlight(request);
                } catch (Exception e) {
                        ex = e;
                }
                assertNotNull(ex);
                verify(fiAdapter, times(1)).cancelReservation(seatReservationId);
                verify(paymentAdapter, times(1)).refund(paymentId);
                verify(loyaltyAdapter, times(1)).reclaimPoints(redemptionId);
                verify(loyaltyAdapter, times(1)).discardPointsEarned(pointsEarningId);
        }

        @ParameterizedTest
        @MethodSource("cancelBookingScenarios")
        public void testCancelBooking(Long seatReservatoionId, Long paymentId, Long redemptionId,
                        Long pointsEarningId) {
                Long bookingId = 123l;
                Booking booking = Booking.builder().id(bookingId).status(BOOKED).seatsReservationId(seatReservatoionId)
                                .paymentId(paymentId).redemptionId(redemptionId).pointsEarningId(pointsEarningId)
                                .build();
                when(repo.findById(bookingId)).thenReturn(Optional.of(booking));
                svc.cancelBooking(bookingId);
                verify(fiAdapter, seatReservatoionId == null ? never() : times(1))
                                .cancelReservation(seatReservatoionId);
                verify(paymentAdapter, paymentId == null ? never() : times(1)).refund(paymentId);
                verify(loyaltyAdapter, redemptionId == null ? never() : times(1)).reclaimPoints(redemptionId);
                verify(loyaltyAdapter, pointsEarningId == null ? never() : times(1))
                                .discardPointsEarned(pointsEarningId);
        }

        private static Stream<Arguments> cancelBookingScenarios() {
                return Stream.of(
                                Arguments.of(234l, 345l, 456l, 567l),
                                Arguments.of(null, 345l, 456l, 567l),
                                Arguments.of(234l, null, 456l, 567l),
                                Arguments.of(234l, 345l, null, 567l),
                                Arguments.of(234l, 345l, 456l, null));
        }

}
