package aloha.spring.microservices.flight_inventory_service.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aloha.spring.microservices.flight_inventory_service.model.FlightInventory;
import aloha.spring.microservices.flight_inventory_service.model.Reservation;
import aloha.spring.microservices.flight_inventory_service.service.FlightInventoryService;

@WebMvcTest(controllers = FlightInventoryController.class)
public class FlightInventoryControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FlightInventoryService svc;

    @Autowired
    private ObjectMapper objMapper;

    @Test
    public void testReserveSeats() throws Exception {
        Long reservationId = 0l, flightId = 1l, bookingId = 2l;
        Integer seatAmount = 3;
        BigDecimal amountDue = BigDecimal.valueOf(100.50);
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .bookingId(bookingId)
                .seatAmount(seatAmount)
                .amountDue(amountDue)
                .flightInventory(FlightInventory.builder().id(flightId).build())
                .build();
        when(svc.reserveSeats(flightId, bookingId, seatAmount)).thenReturn(reservation);
        mvc.perform(post("/flight-inventories/1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objMapper.writeValueAsString(new ReservationRequest(bookingId, seatAmount))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.bookingId").value(bookingId))
                .andExpect(jsonPath("$.seatAmount").value(seatAmount))
                .andExpect(jsonPath("$.amountDue").value(amountDue))
                .andExpect(jsonPath("$.flightInventory").doesNotExist());
    }

    @Test
    public void testCancelReservation() throws Exception {
        Long reservationId = 0l;
        doThrow(new IllegalArgumentException()).when(svc).cancelReservation(argThat(arg -> arg != reservationId));
        mvc.perform(put(String.format("/reservations/%s/cancel", reservationId))).andExpect(status().isNoContent());
        verify(svc, times(1)).cancelReservation(reservationId);
    }

    @Test
    public void testGetReservation() throws JsonProcessingException, Exception {
        Long reservationId = 0l, flightId = 1l, bookingId = 2l;
        Integer seatAmount = 3;
        BigDecimal amountDue = BigDecimal.valueOf(100.50);
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .bookingId(bookingId)
                .seatAmount(seatAmount)
                .amountDue(amountDue)
                .flightInventory(FlightInventory.builder().id(flightId).build())
                .build();
        when(svc.getReservation(reservationId)).thenReturn(reservation);
        mvc.perform(get("/reservations/" + reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.bookingId").value(bookingId))
                .andExpect(jsonPath("$.seatAmount").value(seatAmount))
                .andExpect(jsonPath("$.amountDue").value(amountDue))
                .andExpect(jsonPath("$.flightInventory").doesNotExist());
    }

    @MethodSource("scenariosSearchFlights")
    @ParameterizedTest
    public void testSearchFlights(String origin, String destination, LocalDateTime departureDateTime,
            List<FlightInventory> inventories) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get("/flight-inventories/search");
        if (origin != null) {
            reqBuilder.param("origin", origin);
        }
        if (destination != null) {
            reqBuilder.param("destination", destination);
        }
        if (departureDateTime != null) {
            reqBuilder.param("departureDateTime", departureDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        if (origin == null || destination == null || departureDateTime == null) {
            mvc.perform(reqBuilder).andExpect(status().isBadRequest());
        } else {
            when(svc.searchFlights(origin, destination, departureDateTime)).thenReturn(inventories);
            mvc.perform(reqBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(inventories.size())));
        }
    }

    private static Stream<Arguments> scenariosSearchFlights() {
        String origin = "AAA", destination = "BBB";
        LocalDateTime departureDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        List<FlightInventory> inventories = List.of(FlightInventory.builder().id(1l).build(),
                FlightInventory.builder().id(2l).build());
        return Stream.of(
                Arguments.of(origin, destination, departureDateTime, inventories),
                Arguments.of(null, destination, departureDateTime, inventories),
                Arguments.of(origin, null, departureDateTime, inventories),
                Arguments.of(origin, destination, null, inventories));
    }

}
