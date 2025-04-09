package aloha.spring.microservices.booking_service.service;

import static aloha.spring.microservices.booking_service.model.BookingStatus.*;

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

            // Reserve seats
            SeatReservationRequest seatReserveReq = new SeatReservationRequest(request.getFlight(),
                    booking.getGuests().size());
            SeatReservation reservation = fiAdapter.reserveSeat(seatReserveReq);
            booking.setStatus(SEAT_RESERVED);
            booking.setSeatsReservationId(reservation.getId());
            booking.setAmountDue(reservation.getAmountDue());

            // Redeem points
            booking.setDeductionAmount(BigDecimal.ZERO);
            if (request.getLoyaltyId() != null && request.getRedeemingPoints()) {
                PointsRedemption redemption = loyaltyAdapter.redeemPoints(
                        new PointsRedemptionRequest(booking.getLoyaltyId(), booking.getAmountDue()));
                booking.setStatus(POINTS_REDEEMED);
                booking.setRedemptionId(redemption.getId());
                booking.setPointsRedeemed(redemption.getPointsRedeemed());
                booking.setDeductionAmount(redemption.getDeductionAmount());
                if (!redemption.getRedeemed()) {
                    booking.addMemo("Loyalty points balance is zero. Cannot redeem points.");
                }
            }

            // Make payment
            BigDecimal amountToPay = booking.getAmountDue().subtract(booking.getDeductionAmount());
            Payment payment = paymentAdapter.pay(new PaymentRequest(amountToPay, booking.getCreditCardNumber()));
            booking.setPaymentId(payment.getId());
            booking.setAmountPaid(payment.getAmountPaid());
            booking.setStatus(PAID);

            // Earn points
            PointsEarned pointsEarned = loyaltyAdapter
                    .earnPoints(new PointsEarningRequest(booking.getLoyaltyId(), booking.getAmountPaid()));
            booking.setPointsEarningId(pointsEarned.getId());
            booking.setPointsEarned(pointsEarned.getPoints());

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
        switch (booking.getStatus()) {
            case PAID:
                paymentAdapter.refund(booking.getPaymentId());
            case POINTS_REDEEMED:
                loyaltyAdapter.reclaimPoints(booking.getRedemptionId());
            case SEAT_RESERVED:
                fiAdapter.cancelReservation(booking.getSeatsReservationId());
                break;
            default:
                break;
        }
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
        if (booking.getRedeemPoints() && booking.getRedemptionId() != null) {
            loyaltyAdapter.reclaimPoints(booking.getRedemptionId());
        }
        paymentAdapter.refund(booking.getPaymentId());
        fiAdapter.cancelReservation(booking.getSeatsReservationId());
        booking.setStatus(CANCELLED);
        return repo.save(booking);
    }

}
