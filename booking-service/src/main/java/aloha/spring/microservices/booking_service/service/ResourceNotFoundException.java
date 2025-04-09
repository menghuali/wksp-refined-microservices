package aloha.spring.microservices.booking_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Requested resource is not found.")
public class ResourceNotFoundException extends RuntimeException {
}
