package com.example.demo.Service;

import com.example.demo.Repo.Booking;
import com.example.demo.Repo.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BookingService {
    public List<Booking> getAllBookings();
    public List<Booking> getAllBookingsById(Integer UserId);
    public Optional<Booking> getBookingByBookingId(Integer id);
    public void deleteBooking(int id);
    public Optional<Booking> findBookingById(int id);
    public void SaveBooking(Booking booking) ;
    String AddNewBookingAndUpdateSchedule(User user, String roomId, String date, List<Integer> indices, String reason);
}
