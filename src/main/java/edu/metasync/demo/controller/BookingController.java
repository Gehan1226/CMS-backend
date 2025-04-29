package edu.metasync.demo.controller;

import edu.metasync.demo.dto.booking.BookingCreateRequest;
import edu.metasync.demo.dto.booking.BookingResponse;
import edu.metasync.demo.dto.response.SuccessResponse;
import edu.metasync.demo.dto.response.SuccessResponseWithData;
import edu.metasync.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{userId}")
    public SuccessResponseWithData<List<BookingResponse>> getAllBookingsByUser(@PathVariable Long userId) {
        List<BookingResponse> bookingResponses = bookingService.getAllBookingsByUser(userId);
        return SuccessResponseWithData.<List<BookingResponse>>builder()
                .status(200)
                .message("Bookings retrieved successfully")
                .data(bookingResponses)
                .build();
    }


}
