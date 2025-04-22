package aloha.spring.microservices.booking_service.controller;

import static aloha.spring.microservices.booking_service.model.BookingStatus.BOOKED;
import static aloha.spring.microservices.booking_service.model.BookingStatus.CANCELLED;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import aloha.spring.microservices.booking_service.model.Booking;
import aloha.spring.microservices.booking_service.model.BookingRequest;
import aloha.spring.microservices.booking_service.service.BookingService;
import aloha.spring.microservices.booking_service.service.ResourceNotFoundException;

@WebMvcTest(controllers = { BookingController.class })
public class BookingControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objMapper;

    @MockitoBean
    private BookingService bookingSvc;

    @Test
    public void testGetBooking() throws Exception {
        Booking booking = Booking.builder().id(123l).status(BOOKED).build();
        when(bookingSvc.getBooking(eq(123l))).thenReturn(booking);
        mvc.perform(get("/bookings/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123l))
                .andExpect(jsonPath("$.status").value(BOOKED.toString()));
    }

    @Test
    public void testGetBooking_NotFound() throws Exception {
        when(bookingSvc.getBooking(anyLong())).thenThrow(new ResourceNotFoundException());
        mvc.perform(get("/bookings/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindByName() throws Exception {
        String firstName = "Peter", lastName = "Parker";
        when(bookingSvc.findBookings(eq(firstName), eq(lastName)))
                .thenReturn(List.of(Booking.builder().id(123l).status(BOOKED).build()));
        mvc.perform(get("/bookings/name?firstName=Peter&lastName=Parker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(123))
                .andExpect(jsonPath("$[0].status").value(BOOKED.toString()));
    }

    @Test
    public void testBookFlight() throws Exception {
        BookingRequest br = BookingRequest.builder().firstName("Peter").lastName("Parker").build();
        when(bookingSvc.bookFlight(eq(br))).thenReturn(Booking.builder().id(123l).status(BOOKED).build());
        mvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objMapper.writeValueAsString(br)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.status").value(BOOKED.toString()));
    }

    @Test
    public void testCancelBooking() throws Exception {
        when(bookingSvc.cancelBooking(123l)).thenReturn(Booking.builder().id(123l).status(CANCELLED).build());
        mvc.perform(put("/bookings/123/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.status").value(CANCELLED.toString()));
    }

}
