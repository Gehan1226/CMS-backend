package edu.metasync.demo.dto.booking;

import edu.metasync.demo.dto.auth.UserResponse;
import edu.metasync.demo.dto.service.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long id;
    private String customerName;
    private String address;
    private String date;
    private String time;
    private Service service;
    private UserResponse user;
}
