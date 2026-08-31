package com.example.demo.Controller;

import com.example.demo.Repo.*;
import com.example.demo.Service.BookingService;
import com.example.demo.Service.RoomService;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/MakeBooking")
public class BookingController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private UserService userService;

    @GetMapping()
    public String showAddNewBooking(@RequestParam String roomId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // If the user has not logged in, redirect to the login page
        }
        Room room = roomService.findRoomByid(roomId);
        if (room == null) {
            return "redirect:/welcome?error=Room not found";
        }

        // Prepare model data
        model.addAttribute("room", room);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("booking", new Booking());
        return "UserBookingRoom";
    }

    @PostMapping()
    public ResponseEntity<String> makeBooking(@RequestBody Map<String, Object> requestBody, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String roomId = (String) requestBody.get("roomId");
        String date = (String) requestBody.get("date");
        Integer userId = loggedInUser.getId();
        List<Integer> indices = (List<Integer>) requestBody.get("selectedIndices");
        String reason = (String) requestBody.get("reason");

        if (roomId == null || date == null || userId == null || indices == null || indices.isEmpty()) {
            model.addAttribute("error1", "Missing required fields");
            return ResponseEntity.status(400).body("Missing required fields");
        }
        Optional<User> user = userService.getUserById(loggedInUser.getId());

        String msg = bookingService.AddNewBookingAndUpdateSchedule(user.get(), roomId,date,indices,reason);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime baseTime = LocalTime.of(8, 0);

        if (msg.equals("Selected time slots must be continuous")) return ResponseEntity.status(400).body("Selected time slots must be continuous");
        else if (msg.equals("Please select a time duration between 30 minutes and 4 hours."))  return ResponseEntity.status(400).body("Please select a time duration between 30 minutes and 4 hours.");
        else if (msg.equals("Selected time slots are already booked"))  return ResponseEntity.status(409).body("Selected time slots are already booked");
        else if (msg.equals("Your account is locked. Please contact support.")) return  ResponseEntity.status(403).body("Your account is locked. Please contact support.");
        else if (msg.equals("Booking created successfully.")) {
            userService.updateUserBookingTimes(userId,user.get().getBookingTimes()+1);
            return ResponseEntity.ok("Booking created successfully.");
        } else return ResponseEntity.status(500).body(msg);


    }



    @GetMapping("/schedule")
    public ResponseEntity<?> getScheduleByRoomAndDate(
            @RequestParam String roomId,
            @RequestParam String date,
            Model model) {

        model.addAttribute("roomId", roomId);
        model.addAttribute("date", date);

        try {
            List<Schedule> schedules = scheduleRepo.findByRoomIdAndDate(roomId, date);

            // Turn the time slots into indices (start from 08:00, each 30 mins)
            List<Integer> indices = new ArrayList<>();
            LocalTime baseTime = LocalTime.of(8, 0);

            for (Schedule s : schedules) {
                int index = (int) (Duration.between(baseTime, s.getStartTime()).toMinutes() / 30);
                indices.add(index);
            }

            model.addAttribute("indices", indices);
            return ResponseEntity.ok(indices);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching schedule: " + e.getMessage());
        }
    }
}
