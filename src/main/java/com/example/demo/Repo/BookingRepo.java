package com.example.demo.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    List<Booking> findByRoomIdAndDateAndStartTimeAndEndTime(String RoomId, String date,
                                                            String startTime, String endTime);
    List<Booking> findByRoomId(String roomId);
    public List<Booking> findBookingsByUserId(String UserId);
}
