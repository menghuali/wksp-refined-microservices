package aloha.spring.microservices.booking_service.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/flight-inventory")
@FeignClient(name = "flight-inventory-service")
public interface FlightInventoryAdapter {

    @PostMapping("/reservations")
    SeatReservation reserveSeat(@RequestBody SeatReservationRequest seatReserveReq);

    @PutMapping("/reservations/{id}/cancel")
    void cancelReservation(@PathVariable(name = "id") Long seatsReservationId);

}
