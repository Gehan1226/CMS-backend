package edu.metasync.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.metasync.demo.dto.booking.BookingCreateRequest;
import edu.metasync.demo.entity.BookingEntity;
import edu.metasync.demo.entity.ServiceEntity;
import edu.metasync.demo.entity.UserEntity;
import edu.metasync.demo.exception.UnexpectedException;
import edu.metasync.demo.repository.BookingRepository;
import edu.metasync.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
