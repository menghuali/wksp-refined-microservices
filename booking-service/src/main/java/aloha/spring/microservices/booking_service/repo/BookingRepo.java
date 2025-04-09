package aloha.spring.microservices.booking_service.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloha.spring.microservices.booking_service.model.Booking;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    List<Booking> findByFirstNameAndLastName(String firstName, String lastName);

    List<Booking> findByLoyaltyId(Long loyaltyId);

}
