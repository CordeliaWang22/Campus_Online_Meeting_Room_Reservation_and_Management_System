package com.example.demo.Controller;

import com.example.demo.Repo.*;
import com.example.demo.Service.BookingService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/User/AllBookings")
public class UserControllBooking {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private BookingRepo bookingRepo;

    //View bookings
    @GetMapping
    public String listAllBookings(Model model, HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // If the user has not logged in, redirect to the login page
        }
        System.out.println(loggedInUser.getId());
        List<Booking> allBookings = bookingService.getAllBookingsById(loggedInUser.getId());
        System.out.println();
        model.addAttribute("allBookings", allBookings);  // Prepare model data for passing to the frontend
        return "UserHistoryBooking";  // the html to show
    }
    // Get JSON information according to booking id
    @GetMapping("/{id}")
    @ResponseBody
    public Booking getBookingDetails(@PathVariable("id") Integer id) {
        System.out.println("test");
        Optional<Booking> bookingDetail = bookingService.getBookingByBookingId(id);
        return bookingDetail.get();
    }


    //Delete bookings
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Integer id, Model model) {
        model.addAttribute("id", id);
        Optional<Booking> BookingDbs = bookingService.findBookingById(id);
        if (BookingDbs.isEmpty()) {
            model.addAttribute("error1", "Booking not found");
            return ResponseEntity.status(404).body("Booking not found");
        }
        Booking bookingDbs = BookingDbs.get();
        LocalTime start_time = LocalTime.parse(bookingDbs.getStartTime());
        LocalTime end_time = LocalTime.parse(bookingDbs.getEndTime());
        LocalDate booking_date = LocalDate.parse(bookingDbs.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (LocalDate.now().isAfter(booking_date)){
            model.addAttribute("error2", "Booking deletion failed: Time expired");
            return ResponseEntity.status(400).body("Booking deletion failed: Time expired");
        }
        else if ((!LocalDate.now().isBefore(booking_date)) && LocalTime.now().isAfter(start_time)) {
            model.addAttribute("error2", "Booking deletion failed: Time expired");
            return ResponseEntity.status(400).body("Booking deletion failed: Time expired");
        }
        // Count the corresponding affected time slots in Schedule
        List<LocalTime> affectedTimes = new ArrayList<>();
        LocalTime current = start_time;
        while (current.isBefore(end_time)) {
            affectedTimes.add(current);
            current = current.plusMinutes(30);
        }
        // Delete these in Schedule
        scheduleRepo.deleteByRoomIdAndDateAndStartTimeIn(bookingDbs.getRoomId(), bookingDbs.getDate(), affectedTimes);
        bookingService.deleteBooking(id);
        model.addAttribute("success", "Booking deleted successfully");
        return ResponseEntity.ok("Booking deleted successfully");
    }



    //Update bookings
    private static final List<Integer> ALLOWED_DURATIONS = List.of(30, 60, 90, 120, 150, 180, 210, 240);
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBooking(@PathVariable Integer id, @RequestBody Booking updatedBooking, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("updatedBooking", updatedBooking);

        Optional<Booking> BookingDbs = bookingService.findBookingById(id);
        if (BookingDbs.isEmpty()) {
            model.addAttribute("error1", "Booking not found");
            return ResponseEntity.status(404).body("Booking not found");
        }
        Booking existingBooking = BookingDbs.get();
        LocalTime start_time = LocalTime.parse(existingBooking.getStartTime());
        LocalDate start_date = LocalDate.parse(existingBooking.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (LocalDate.now().isAfter(start_date)){
            model.addAttribute("error2", "Booking updating failed: Time expired");
            return ResponseEntity.status(400).body("Booking updating failed: Time expired");
        }
        else if ((!LocalDate.now().isBefore(start_date)) && LocalTime.now().isAfter(start_time)) {
            model.addAttribute("error2", "Booking updating failed: Time expired");
            return ResponseEntity.status(400).body("Booking updating failed: Time expired");
        }

        String newRoomId = (updatedBooking.getRoomId() == null || updatedBooking.getRoomId().isEmpty())
                ? existingBooking.getRoomId()
                : updatedBooking.getRoomId();

        String newStartTimeStr = (updatedBooking.getStartTime() == null || updatedBooking.getStartTime().isEmpty())
                ? existingBooking.getStartTime()
                : updatedBooking.getStartTime();

        String newEndTimeStr = (updatedBooking.getEndTime() == null || updatedBooking.getEndTime().isEmpty())
                ? existingBooking.getEndTime()
                : updatedBooking.getEndTime();

        String newDateStr = (updatedBooking.getDate() == null || updatedBooking.getDate().isEmpty())
                ? existingBooking.getDate()
                : updatedBooking.getDate();

        LocalDate newDate = LocalDate.parse(newDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime newStartTime = LocalTime.parse(newStartTimeStr);
        LocalTime newEndTime = LocalTime.parse(newEndTimeStr);

        String oldRoomId = existingBooking.getRoomId();
        LocalDate oldDate = LocalDate.parse(existingBooking.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime oldStartTime = LocalTime.parse(existingBooking.getStartTime());
        LocalTime oldEndTime = LocalTime.parse(existingBooking.getEndTime());

        int duration = (int) Duration.between(newStartTime, newEndTime).toMinutes();

        if (newDate.isBefore(LocalDate.now())|| newDate.isAfter(LocalDate.now().plusDays(7))) {
            model.addAttribute("error3", "Booking updating failed: You can only book within 7 days in advance");
            return ResponseEntity.status(400).body("Booking updating failed: You can only book within 7 days in advance");
        }

        // Check if the duration selected is allowed
        if (!ALLOWED_DURATIONS.contains(duration)) {
            model.addAttribute("error4", "Booking updating failed: Duration is not allowed");
            return ResponseEntity.status(400).body("Booking updating failed: Duration is not allowed");
        }


        // Original booking time --> time slots in Schedule
        List<LocalTime> oldTimes = new ArrayList<>();
        LocalTime tempTime = oldStartTime;
        while (tempTime.isBefore(oldEndTime)) {
            oldTimes.add(tempTime);
            tempTime = tempTime.plusMinutes(30);
        }

        // Updated booking time --> time slots in Schedule
        List<LocalTime> newTimes = new ArrayList<>();
        tempTime = newStartTime;
        while (tempTime.isBefore(newEndTime)) {
            newTimes.add(tempTime);
            tempTime = tempTime.plusMinutes(30);
        }

        // Temporarily set the original booking time as available
        scheduleRepo.deleteByRoomIdAndDateAndStartTimeIn(oldRoomId, existingBooking.getDate(), oldTimes);

        // Check if new time slots are available
        List<Schedule> existingSchedules = scheduleRepo.findByRoomIdAndDateAndStartTimes(newRoomId, newDateStr, newTimes);
        if (!existingSchedules.isEmpty()) {
            // If conflict exists, restore old schedule records
            List<Schedule> schedulesToRestore = new ArrayList<>();
            for (LocalTime time : oldTimes) {
                schedulesToRestore.add(new Schedule(time, existingBooking.getDate(), oldRoomId, false));
            }
            scheduleRepo.saveAll(schedulesToRestore);

            model.addAttribute("error5", "Booking update failed: New time slot is already booked");
            return ResponseEntity.status(400).body("Booking update failed: New time slot is already booked");
        }

        // Create new schedule records (make unavailable)
        List<Schedule> newSchedules = new ArrayList<>();
        for (LocalTime time : newTimes) {
            newSchedules.add(new Schedule(time, newDateStr, newRoomId, false));
        }
        scheduleRepo.saveAll(newSchedules);

        // Update the booking in the database
        existingBooking.setRoomId(newRoomId);
        existingBooking.setDate(newDateStr);
        existingBooking.setStartTime(newStartTimeStr);
        existingBooking.setEndTime(newEndTimeStr);
        bookingRepo.save(existingBooking);

        model.addAttribute("success", "Booking updated successfully");
        return ResponseEntity.ok("Booking updated successfully");
    }


    //Get all the available start times to display in start time options in the frontend
    @GetMapping("/AvailableStartTimes")
    public ResponseEntity<List<String>> getAvailableStartTimes(
            @RequestParam String roomId,
            @RequestParam String dateStr,
            @RequestParam(required = false) Integer currentBookingId,
            Model model) {
        System.out.println("test start");
        model.addAttribute("roomId", roomId);
        model.addAttribute("dateStr", dateStr);

        // Date within 7 days
        LocalDate selectedDate = LocalDate.parse(dateStr);
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(6);

        if (selectedDate.isBefore(today) || selectedDate.isAfter(maxDate)) {
            return ResponseEntity.badRequest().body(null);
        }

        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(22, 0);
        LocalTime now = LocalTime.now();

        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime time = start;
        while (time.isBefore(end)) {
            // If the user chooses today, narrow down the range to the time slots after the current time
            if (!selectedDate.equals(today) || time.isAfter(now)) {
                allSlots.add(time);
            }
            time = time.plusMinutes(30);
        }

        // Get the start times occupied by existing bookings
        List<Schedule> bookedSchedules = scheduleRepo.findByRoomIdAndDate(roomId, dateStr);
        List<LocalTime> occupiedTimes = new ArrayList<>();
        for (Schedule s : bookedSchedules) {
            if (currentBookingId != null) {
                Optional<Booking> currentBooking = bookingRepo.findById(currentBookingId);
                if (currentBooking.isPresent()) {
                    Booking booking = currentBooking.get();
                    LocalTime bookingStart = LocalTime.parse(booking.getStartTime());
                    LocalTime bookingEnd = LocalTime.parse(booking.getEndTime());

                    if (s.getStartTime().isAfter(bookingStart.minusSeconds(1)) &&
                            s.getStartTime().isBefore(bookingEnd)) {
                        continue;
                    }
                }
            }
            occupiedTimes.add(s.getStartTime());
        }

        // Remove occupied start times
        List<String> availableSlots = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (LocalTime slot : allSlots) {
            if (!occupiedTimes.contains(slot)) {
                availableSlots.add(slot.format(formatter));
            }
        }

        model.addAttribute("availableSlots", availableSlots);
        return ResponseEntity.ok(availableSlots);
    }

    @GetMapping("/AvailableEndTimes")
    public ResponseEntity<List<String>> getAvailableEndTimes(
            @RequestParam String roomId,
            @RequestParam String dateStr,
            @RequestParam String startTimeStr,
            @RequestParam(required = false) Integer currentBookingId,
            Model model) {
        System.out.println("test end");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime selectedStartTime = LocalTime.parse(startTimeStr, timeFormatter);
        LocalTime latestEndTime = LocalTime.of(22, 0);

        // Get the end times occupied by existing bookings
        List<Schedule> bookedSchedules = scheduleRepo.findByRoomIdAndDate(roomId, dateStr);
        List<LocalTime> occupiedTimes = new ArrayList<>();
        for (Schedule s : bookedSchedules) {
            if (currentBookingId != null) {
                Optional<Booking> currentBooking = bookingRepo.findById(currentBookingId);
                if (currentBooking.isPresent()) {
                    Booking booking = currentBooking.get();
                    LocalTime bookingStart = LocalTime.parse(booking.getStartTime());
                    LocalTime bookingEnd = LocalTime.parse(booking.getEndTime());

                    if (s.getStartTime().isAfter(bookingStart.minusSeconds(1)) &&
                            s.getStartTime().isBefore(bookingEnd)) {
                        continue;
                    }
                }
            }
            occupiedTimes.add(s.getStartTime());
        }

        List<String> availableEndTimes = new ArrayList<>();
        LocalTime currentEnd = selectedStartTime.plusMinutes(30);

        while (currentEnd.isAfter(selectedStartTime) && currentEnd.isBefore(latestEndTime.plusSeconds(1))) {
            boolean conflict = false;
            LocalTime cursor = selectedStartTime;
            while (cursor.isBefore(currentEnd)) {
                if (occupiedTimes.contains(cursor)) {
                    conflict = true;
                    break;
                }
                cursor = cursor.plusMinutes(30);
            }

            if (conflict) break;
            availableEndTimes.add(currentEnd.format(timeFormatter));
            currentEnd = currentEnd.plusMinutes(30);
        }

        model.addAttribute("availableEndTimes", availableEndTimes);
        return ResponseEntity.ok(availableEndTimes);
    }
}
