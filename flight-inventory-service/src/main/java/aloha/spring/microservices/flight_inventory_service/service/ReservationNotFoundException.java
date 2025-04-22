package aloha.spring.microservices.flight_inventory_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ReservationNotFoundException extends RuntimeException {
}
