package aloha.spring.microservices.flight_inventory_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NoEnoughSeatException extends RuntimeException {
}
