package com.example.demo.Service;

import com.example.demo.Repo.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public interface adminBookingService {
    List<Booking> getAllBookings();
    Booking getBookingById(Integer id);
    List<Booking> getBookingsByUserId(String userId);
    Page<Booking> getBookingsByUserId(String userId, Pageable pageable);//added

    List<Booking> getBookingsByStatus(String status, LocalDate currentDate, LocalTime currentTime);
    Page<Booking> getBookingsByStatus(String status, LocalDate currentDate, LocalTime currentTime, Pageable pageable);
    void deleteBooking(Integer id);

    Page<Booking> getAllBookings(Pageable pageable);

}