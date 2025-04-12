package aloha.spring.microservices.booking_service.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/loyalty")
@FeignClient("loyalty-service")
public interface LoyaltyAdapter {

    @PostMapping("/redemptions")
    PointsRedemption redeemPoints(@RequestBody PointsRedemptionRequest pointsRedemptionRequest);

    @PutMapping("/redemptions/{id}/cancel")
    void cancelRedemption(@PathVariable(name = "id") Long redemptionId);

    @PostMapping("/earnings")
    PointsEarned earnPoints(@RequestBody PointsEarningRequest pointsEarningRequest);

    @PutMapping("/earnings/{id}/cancel")
    void cancelPointsEarned(@PathVariable(name = "id") Long pointsEarningId);

}
