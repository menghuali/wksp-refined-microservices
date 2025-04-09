package aloha.spring.microservices.flight_booking_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FlightBookingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightBookingServiceApplication.class, args);
	}

}
