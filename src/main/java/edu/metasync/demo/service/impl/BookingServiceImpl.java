package edu.metasync.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.metasync.demo.dto.booking.BookingCreateRequest;
import edu.metasync.demo.dto.booking.BookingResponse;
import edu.metasync.demo.dto.booking.BookingUpdateRequest;
import edu.metasync.demo.entity.BookingEntity;
import edu.metasync.demo.entity.ServiceEntity;
import edu.metasync.demo.entity.UserEntity;
import edu.metasync.demo.exception.UnauthorizedException;
import edu.metasync.demo.exception.UnexpectedException;
import edu.metasync.demo.repository.BookingRepository;
import edu.metasync.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void createBooking(BookingCreateRequest bookingEntity) {
        try {
            BookingEntity booking = objectMapper.convertValue(bookingEntity, BookingEntity.class);
            booking.setUser(UserEntity.builder()
                    .id(bookingEntity.getUserId())
                    .build());
            booking.setService(ServiceEntity.builder()
                    .id(bookingEntity.getServiceId())
                    .build());
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new UnexpectedException(
                    "An unexpected error occurred while creating the booking. " +
                            "Please check the provided values and try again.");
        }

    }

    @Override
    public List<BookingResponse> getAllBookingsByUser(Long userId) {
        try {
            List<BookingEntity> bookingEntities = bookingRepository.findAllByUserId(userId);
            return bookingEntities.stream()
                    .map(booking -> objectMapper.convertValue(booking, BookingResponse.class))
                    .toList();
        } catch (Exception e) {
            throw new UnexpectedException(
                    "An unexpected error occurred while retrieving the bookings for the user.");
        }
    }

    @Override
    public void updateBooking(Long bookingId, BookingUpdateRequest bookingEntity) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UnexpectedException("Booking not found with id: " + bookingId));

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!booking.getUser().getUserName().equals(loggedInUser)) {
            throw new UnauthorizedException("You are not authorized to update this booking.");
        }

        try {
            booking.setCustomerName(bookingEntity.getCustomerName());
            booking.setAddress(bookingEntity.getAddress());
            booking.setDate(bookingEntity.getDate());
            booking.setTime(bookingEntity.getTime());
            booking.setService(ServiceEntity.builder()
                    .id(bookingEntity.getServiceId())
                    .build());
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new UnexpectedException(
                    "An unexpected error occurred while updating the booking. " +
                            "Please check the provided values and try again.");
        }
    }

    @Override
    public void deleteBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UnexpectedException("Booking not found with id: " + bookingId));

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!booking.getUser().getUserName().equals(loggedInUser)) {
            throw new UnauthorizedException("You are not authorized to delete this booking.");
        }

        try {
            bookingRepository.delete(booking);
        } catch (Exception e) {
            throw new UnexpectedException(
                    "An unexpected error occurred while deleting the booking.");
        }
    }
}
