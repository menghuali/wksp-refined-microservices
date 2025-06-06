package aloha.spring.microservices.booking_service.service;

import static aloha.spring.microservices.booking_service.model.BookingStatus.BOOKED;
import static aloha.spring.microservices.booking_service.model.BookingStatus.CANCELLED;
import static aloha.spring.microservices.booking_service.model.BookingStatus.NEW;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aloha.spring.microservices.booking_service.adapter.FlightInventoryAdapter;
import aloha.spring.microservices.booking_service.adapter.LoyaltyAdapter;
import aloha.spring.microservices.booking_service.adapter.Payment;
import aloha.spring.microservices.booking_service.adapter.PaymentAdapter;
import aloha.spring.microservices.booking_service.adapter.PaymentRequest;
import aloha.spring.microservices.booking_service.adapter.PointsEarned;
import aloha.spring.microservices.booking_service.adapter.PointsEarningRequest;
import aloha.spring.microservices.booking_service.adapter.PointsRedemption;
import aloha.spring.microservices.booking_service.adapter.PointsRedemptionRequest;
import aloha.spring.microservices.booking_service.adapter.SeatReservation;
import aloha.spring.microservices.booking_service.adapter.SeatReservationRequest;
import aloha.spring.microservices.booking_service.model.Booking;
import aloha.spring.microservices.booking_service.model.BookingRequest;
import aloha.spring.microservices.booking_service.repo.BookingRepo;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepo repo;
    private FlightInventoryAdapter fiAdapter;
    private LoyaltyAdapter loyaltyAdapter;
    private PaymentAdapter paymentAdapter;

    @Override
    public Booking getBooking(Long id) {
        Optional<Booking> booking = repo.findById(id);
        if (!booking.isPresent())
            throw new ResourceNotFoundException();
        return booking.get();
    }

    @Override
    public List<Booking> findBookings(String firstName, String lastName) {
        return repo.findByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public List<Booking> findBookings(Long loyaltyId) {
        return repo.findByLoyaltyId(loyaltyId);
    }

    @Transactional
    @Override
    public Booking bookFlight(BookingRequest request) {
        Booking booking = null;
        try {
            // Create a booking
            booking = Booking.builder().status(NEW).flight(request.getFlight()).firstName(request.getFirstName())
                    .lastName(request.getLastName()).guests(request.getGuests())
                    .creditCardNumber(request.getCrediCardNumber()).loyaltyId(request.getLoyaltyId()).build();

            // Generate flight ID
            booking.getFlight().initFlightId();

            // Reserve seats
            SeatReservationRequest seatReserveReq = new SeatReservationRequest(request.getFlight(),
                    booking.getGuests().size());
            SeatReservation reservation = fiAdapter.reserveSeat(seatReserveReq);
            booking.setSeatsReservationId(reservation.getId());
            booking.setAmountDue(reservation.getAmountDue());

            // Redeem points
            booking.setPointsRedeemed(0);
            booking.setDeductionAmount(BigDecimal.ZERO);
            if (request.getLoyaltyId() != null && request.getRedeemingPoints()) {
                PointsRedemption redemption = loyaltyAdapter.redeemPoints(
                        new PointsRedemptionRequest(booking.getLoyaltyId(), booking.getAmountDue()));
                booking.setRedemptionId(redemption.getId());
                booking.setPointsRedeemed(redemption.getPointsRedeemed());
                booking.setDeductionAmount(redemption.getDeductionAmount());
            }

            // Make payment
            BigDecimal amountToPay = booking.getAmountDue().subtract(booking.getDeductionAmount());
            Payment payment = paymentAdapter.pay(new PaymentRequest(amountToPay, booking.getCreditCardNumber()));
            booking.setPaymentId(payment.getId());
            booking.setAmountPaid(payment.getAmountPaid());

            // Earn points
            booking.setPointsEarned(0);
            if (request.getLoyaltyId() != null) {
                PointsEarned pointsEarned = loyaltyAdapter
                        .earnPoints(new PointsEarningRequest(booking.getLoyaltyId(), booking.getAmountPaid()));
                booking.setPointsEarningId(pointsEarned.getId());
                booking.setPointsEarned(pointsEarned.getPoints());
            }

            booking.setStatus(BOOKED);
            // Save and return the booking
            return repo.save(booking);
        } catch (Exception e) {
            // Rollback booking if exception occurs
            rollbackBooking(booking);
            throw new BookingServiceException(e);
        }
    }

    private void rollbackBooking(Booking booking) {
        if (booking.getPointsEarningId() != null)
            loyaltyAdapter.cancelPointsEarned(booking.getPointsEarningId());

        if (booking.getPaymentId() != null)
            paymentAdapter.refund(booking.getPaymentId());

        if (booking.getRedemptionId() != null)
            loyaltyAdapter.cancelRedemption(booking.getRedemptionId());

        if (booking.getSeatsReservationId() != null)
            fiAdapter.cancelReservation(booking.getSeatsReservationId());
    }

    @Transactional
    @Override
    public Booking cancelBooking(Long id) {
        Optional<Booking> optional = repo.findById(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException();
        }
        Booking booking = optional.get();
        if (booking.getStatus() == CANCELLED) {
            return booking;
        }
        rollbackBooking(booking);
        booking.setStatus(CANCELLED);
        return repo.save(booking);
    }

}
