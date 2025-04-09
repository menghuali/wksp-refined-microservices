package aloha.spring.microservices.booking_service.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class Booking {

    private Long id;
    private String carrier;
    private Flight flight;
    private Long seatsReservationId;
    private String firstName;
    private String lastName;
    private List<Guest> guests;
    private BookingStatus status;
    private String creditCardNumber;
    private BigDecimal amountDue;
    private Long loyaltyId;
    private Boolean redeemPoints;
    private Long redemptionId;
    private Integer pointsRedeemed;
    private Boolean earnPoints;
    private Long pointsEarningId;
    private Integer pointsEarned;
    private BigDecimal deductionAmount;
    private BigDecimal amountPaid;
    private Long paymentId;
    private List<String> memos;

    public Booking() {
        memos = new ArrayList<>();
    }

    public void addMemo(String memo) {
        memos.add(memo);
    }

}
