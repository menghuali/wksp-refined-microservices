package aloha.spring.microservices.booking_service.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
@Entity
public class Booking {

    @Id
    @GeneratedValue
    private Long id;
    private String carrier;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    private Long seatsReservationId;
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
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

}
