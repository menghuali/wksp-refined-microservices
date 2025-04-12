package aloha.spring.microservices.booking_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloha.spring.microservices.booking_service.model.Guest;

@Repository
public interface GuestRepo extends JpaRepository<Guest, Long> {
}
