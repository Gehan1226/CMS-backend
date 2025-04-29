package edu.metasync.demo.service;

import edu.metasync.demo.dto.booking.BookingCreateRequest;

public interface BookingService {
    void createBooking(BookingCreateRequest bookingEntity);
}
