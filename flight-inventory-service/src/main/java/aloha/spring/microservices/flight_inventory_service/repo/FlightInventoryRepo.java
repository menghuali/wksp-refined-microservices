package aloha.spring.microservices.flight_inventory_service.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloha.spring.microservices.flight_inventory_service.model.FlightInventory;

@Repository
public interface FlightInventoryRepo extends JpaRepository<FlightInventory, Long> {

    List<FlightInventory> findByOriginAndDestinationAndDepartureDateTime(String origin, String destination,
            LocalDateTime departureDateTime);

}
