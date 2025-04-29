package edu.metasync.demo.service;

import edu.metasync.demo.dto.booking.BookingCreateRequest;
import edu.metasync.demo.dto.booking.BookingResponse;
import edu.metasync.demo.dto.booking.BookingUpdateRequest;

import java.util.List;

public interface BookingService {
    void createBooking(BookingCreateRequest bookingEntity);

    List<BookingResponse> getAllBookingsByUser(Long userId);

    void updateBooking(Long bookingId, BookingUpdateRequest bookingEntity);

    void deleteBooking(Long bookingId);
}
