package aloha.spring.microservices.booking_service.adapter;

public interface LoyaltyAdapter {

    PointsRedemption redeemPoints(PointsRedemptionRequest pointsRedemptionRequest);

    void reclaimPoints(Long redemptionId);

    PointsEarned earnPoints(PointsEarningRequest pointsEarningRequest);

    void discardPointsEarned(Long pointsEarningId);

}
