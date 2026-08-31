package com.example.demo.Controller;

import com.example.demo.Repo.Room;
import com.example.demo.Repo.User;
import com.example.demo.Service.RoomService;
import com.example.demo.Service.ScheduleService;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/welcome")
public class UserSearchRoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UserService userService;

    @GetMapping()
    public String ALLRooms(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // If the user is not logged in, redirect to the login page
        }
        Optional<User> userDbs = userService.getUserById(loggedInUser.getId());
        User user = userDbs.get();
        if (user.getAvatar() == null) userService.updateUserAvatar(user.getId(), "/images/default-avatar.jpg");

        if (user.getName() == null) {
            System.out.println("enter");
            userService.updateUserName(user.getId(), "User");
            return "redirect:/welcome";
        }
        System.out.println("nametest:" + user.getName());


        Set<Room> roomsDbs = roomService.findAll();
        Iterator<Room> iterator = roomsDbs.iterator();
        // Get all the rooms that are currently available
        while (iterator.hasNext()) {
            Room room = iterator.next();
            if (!room.getAvailability()) {
                iterator.remove();
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("filteredRooms", roomsDbs);


        return "UserHomePage";
    }

    @PostMapping("/search")
    @ResponseBody
    //Search for the room with the specified Room ID
    public Map<String, Object> searchRooms(@RequestParam(name = "location", required = false) String location) {

        Room roomDbs = roomService.findRoomByid(location);
        Map<String, Object> response = new HashMap<>();

        if (roomDbs == null) {
            response.put("error", true);
            response.put("message", "No matching rooms found for the specified location.");
        } else if (roomDbs != null && roomDbs.getAvailability() == false) {
            response.put("error", true);
            response.put("message", "No matching rooms found for the specified location.");
        } else {
            Set<Room> filteredRooms = new HashSet<>();
            filteredRooms.add(roomDbs);
            response.put("error", false);
            response.put("rooms", filteredRooms);
        }
        return response;
    }


    @PostMapping
    @ResponseBody
    //Filter rooms that meet the specified criteria
    public Map<String, Object> filterRooms(
            @RequestParam(name = "Date", required = false) String date,
            @RequestParam(name = "Start_time", required = false) LocalTime start_time,
            @RequestParam(name = "End_time", required = false) LocalTime end_time,
            //The values sent from the frontend should be 0, 1, 2, 3, or 4, representing the indices of the respective capacities
            @RequestParam(name = "Capacity", required = false) Integer capacity,
            //The frontend should send a set of IT names
            @RequestParam(name = "IT_facility", required = false) List<String> it_facility) {


        Set<Room> roomsDbs = new HashSet<>();


        //The filter condition includes the date but not the time
        if (date != null && start_time == null && end_time == null) {
            Set<Room> dateRooms = scheduleService.findRoomByDate(date);
            if (dateRooms != null) {
                roomsDbs = dateRooms;
            }

        }
        //The filter condition includes the time but not the date
        else if (date == null && (start_time != null && end_time != null)) {
            Set<Room> timeRooms = scheduleService.findRoomByTime(start_time, end_time);
            if (timeRooms != null) {
                roomsDbs = timeRooms;
            }
            System.out.println("2");
        }
        //The filter condition includes both date and time
        else if (date != null && (start_time != null && end_time != null)) {
            Set<Room> dateTimeRooms = scheduleService.findRoomByDateAndTime(date, start_time, end_time);
            if (dateTimeRooms != null) {
                roomsDbs = dateTimeRooms;
            }
            System.out.println("3");
        } else {
            roomsDbs = roomService.findAll();
        }
        //The filter condition includes capacity
        if (capacity != null) {
            roomsDbs.removeIf(room -> {
                int cap = room.getCapacity();
                return switch (capacity) {
                    case 0 -> false;
                    case 1 -> !(cap >= 1 && cap <= 5);
                    case 2 -> !(cap >= 6 && cap <= 10);
                    case 3 -> !(cap >= 11 && cap <= 20);
                    case 4 -> !(cap >= 21);
                    default -> true;
                };
            });
        }
        //The filter condition includes it_facility
        if (it_facility != null && !it_facility.isEmpty()) {
            roomsDbs.removeIf(room -> {

                if (room.getItFacility() != null) {
                    String roomIt = room.getItFacility();
                    return it_facility.stream().anyMatch(it -> !roomIt.contains(it));
                } else return true;

            });
        }
        roomsDbs.removeIf(room -> !room.getAvailability());
        Map<String, Object> response = new HashMap<>();
        if (roomsDbs.isEmpty()) {
            response.put("error", true);
            response.put("message", "No matching rooms found for the selected filters.");
        } else {
            response.put("error", false);
            response.put("rooms", roomsDbs);
        }
        return response;
    }


}
