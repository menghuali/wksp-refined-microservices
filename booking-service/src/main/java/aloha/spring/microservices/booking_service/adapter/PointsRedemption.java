package aloha.spring.microservices.booking_service.adapter;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class PointsRedemption {

    private Long id;
    private Boolean redeemed;
    private Integer pointsRedeemed;
    private BigDecimal deductionAmount;
    private Integer pointsBalance;

    @JsonCreator
    public PointsRedemption(@JsonProperty("id") Long id, @JsonProperty("redeemed") Boolean redeemed,
            @JsonProperty("pointsRedeemed") Integer pointsRedeemed,
            @JsonProperty("deductionAmount") BigDecimal deductionAmount,
            @JsonProperty("pointsBalance") Integer pointsBalance) {
        this.id = id;
        this.redeemed = redeemed;
        this.pointsRedeemed = pointsRedeemed;
        this.deductionAmount = deductionAmount;
        this.pointsBalance = pointsBalance;
    }

}
