package com.fbs.booking.controller;

import com.fbs.booking.dto.BookingRequest;
import com.fbs.booking.dto.BookingResponse;
import com.fbs.booking.entity.BookingStatus;
import com.fbs.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBooking_Success() throws Exception {
        // Arrange
        UUID flightId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        BookingRequest request = new BookingRequest(flightId, "test@example.com", 2);

        BookingResponse response = new BookingResponse();
        response.setBookingId(bookingId);
        response.setFlightId(flightId);
        response.setCustomerEmail("test@example.com");
        response.setNumberOfSeats(2);
        response.setStatus(BookingStatus.CONFIRMED);
        response.setTotalAmount(BigDecimal.valueOf(299.99));
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(bookingId.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.customerEmail").value("test@example.com"))
                .andExpect(jsonPath("$.numberOfSeats").value(2));
    }

    @Test
    void createBooking_InvalidRequest() throws Exception {
        // Arrange - Missing required fields
        BookingRequest invalidRequest = new BookingRequest();

        // Act & Assert
        mockMvc.perform(post("/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById_Success() throws Exception {
        // Arrange
        UUID bookingId = UUID.randomUUID();
        BookingResponse response = new BookingResponse();
        response.setBookingId(bookingId);
        response.setStatus(BookingStatus.CONFIRMED);

        when(bookingService.getBookingById(bookingId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/bookings/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(bookingId.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void getBookingsByEmail_Success() throws Exception {
        // Arrange
        String email = "test@example.com";
        BookingResponse response1 = new BookingResponse();
        response1.setBookingId(UUID.randomUUID());
        response1.setCustomerEmail(email);

        BookingResponse response2 = new BookingResponse();
        response2.setBookingId(UUID.randomUUID());
        response2.setCustomerEmail(email);

        when(bookingService.getBookingsByCustomerEmail(email))
                .thenReturn(Arrays.asList(response1, response2));

        // Act & Assert
        mockMvc.perform(get("/v1/bookings")
                .param("customerEmail", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerEmail").value(email))
                .andExpect(jsonPath("$[1].customerEmail").value(email));
    }
}