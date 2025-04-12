package aloha.spring.microservices.booking_service.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Flight {

    @Id
    private String id;

    private String carrier;
    private String number;
    private String origin;
    private String destination;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime departureDateTime;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    public void initFlightId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        id = String.format("%s:%s:%s:%s:%s", carrier, number, origin, destination, formatter.format(departureDateTime));
    }

}
