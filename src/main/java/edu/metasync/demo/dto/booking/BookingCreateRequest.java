package edu.metasync.demo.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateRequest {
    private String customerName;
    private String address;
    private LocalDate date;
    private LocalTime time;
    private Long serviceId;
    private Long userId;
}
