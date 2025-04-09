package aloha.spring.microservices.booking_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class BookingServiceException extends RuntimeException {

    public BookingServiceException(Exception cause) {
        super(cause);
    }
}
