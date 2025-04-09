package aloha.spring.microservices.booking_service.adapter;

public interface FlightInventoryAdapter {

    SeatReservation reserveSeat(SeatReservationRequest seatReserveReq);

    void cancelReservation(Long seatsReservationId);

}
