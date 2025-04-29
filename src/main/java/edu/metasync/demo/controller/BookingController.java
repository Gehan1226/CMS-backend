package edu.metasync.demo.controller;

import edu.metasync.demo.dto.booking.BookingCreateRequest;
import edu.metasync.demo.dto.response.SuccessResponse;
import edu.metasync.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public SuccessResponse createBooking(@RequestBody BookingCreateRequest bookingCreateRequest) {
        bookingService.createBooking(bookingCreateRequest);
        return SuccessResponse.builder()
                .status(200)
                .message("Booking created successfully")
                .build();
    }

}
