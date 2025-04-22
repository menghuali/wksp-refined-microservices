package aloha.spring.microservices.flight_inventory_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloha.spring.microservices.flight_inventory_service.model.Reservation;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {
}
