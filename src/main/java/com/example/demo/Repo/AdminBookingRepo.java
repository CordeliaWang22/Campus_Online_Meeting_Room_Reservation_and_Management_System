package com.example.demo.Repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AdminBookingRepo extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(String userId);

    // search the finished booking
    @Query("SELECT b FROM Booking b WHERE b.date < :currentDate OR (b.date = :currentDate AND b.endTime < :currentTime)")
    List<Booking> findFinishedBookings(LocalDate currentDate, LocalTime currentTime);

    // search processing booking
    @Query("SELECT b FROM Booking b WHERE b.date > :currentDate OR (b.date = :currentDate AND b.endTime >= :currentTime)")
    List<Booking> findProcessingBookings(LocalDate currentDate, LocalTime currentTime);
}
