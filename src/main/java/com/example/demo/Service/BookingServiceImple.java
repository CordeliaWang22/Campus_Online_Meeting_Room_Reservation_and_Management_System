package com.example.demo.Service;

import com.example.demo.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImple implements BookingService {
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private UserService userService;


    @Autowired
    private RoomRepo roomRepository;


    @Override
    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    @Override
    public List<Booking> getAllBookingsById(Integer UserId) {
        return bookingRepo.findBookingsByUserId(UserId.toString());
    }

    @Override
    public Optional<Booking> getBookingByBookingId(Integer id) {
        return bookingRepo.findById(id);
    }

    @Override
    public void deleteBooking(int id) {
        bookingRepo.deleteById(id);
    }

    @Override
    public Optional<Booking> findBookingById(int id) {
        return bookingRepo.findById(id);
    }

    @Override
    public void SaveBooking(Booking booking) {
        bookingRepo.save(booking);
    }

    @Override
    public String AddNewBookingAndUpdateSchedule(User user, String roomId, String date, List<Integer> indices, String reason) {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime baseTime = LocalTime.of(8, 0);

        // Check whether the selected time slots are continuous
        indices.sort(Integer::compareTo);
        for (int i = 1; i < indices.size(); i++) {
            if (indices.get(i) != indices.get(i - 1) + 1) {
                return "Selected time slots must be continuous";
            }
        }

        // Check if the duration is allowed
        LocalTime startTime = baseTime.plusMinutes(indices.get(0) * 30L);
        LocalTime endTime = baseTime.plusMinutes((indices.get(indices.size() - 1) + 1) * 30L);
        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();
        if (!(minutes >= 30 && minutes <= 240)) return "Please select a time duration between 30 minutes and 4 hours.";

        // Check if the booking conflicts with existing bookings
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime t = startTime;
        while (t.isBefore(endTime)) {
            timeSlots.add(t);
            t = t.plusMinutes(30);
        }

        List<Schedule> existing = scheduleRepo.findByRoomIdAndDateAndStartTimes(roomId, date, timeSlots);
        if (!existing.isEmpty()) {
            return "Selected time slots are already booked";
        }

        // Check if the user is locked
        if (user.getStatus().equals("lock")) {
            return "Your account is locked. Please contact support.";

        }


        try {
            List<Schedule> newSchedules = new ArrayList<>();
            for (LocalTime slot : timeSlots) {
                newSchedules.add(new Schedule(slot, date, roomId, false));
            }
            scheduleRepo.saveAll(newSchedules);

            Booking newBooking = new Booking();
            newBooking.setRoomId(roomId);
            newBooking.setDate(date);
            newBooking.setStartTime(startTime.format(timeFormatter));
            newBooking.setEndTime(endTime.format(timeFormatter));
            newBooking.setUserId(user.getId().toString());
            newBooking.setReason(reason);

            bookingRepo.save(newBooking);

            return "Booking created successfully.";
        } catch (Exception e) {
            return "Error occurred while saving booking: " + e.getMessage();
        }


    }


}
