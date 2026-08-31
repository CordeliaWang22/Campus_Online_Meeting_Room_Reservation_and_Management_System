package com.example.demo.Controller;


import com.example.demo.Repo.Booking;
import com.example.demo.Service.BookingService;
import com.example.demo.Service.adminBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin/booking")
public class adminBookingController {

    @Autowired
    private adminBookingService adminBookingService;

    @GetMapping
    public String getAllBookings(
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        int pageSize = 10; // display 10 item one page
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Page<Booking> bookingPage = adminBookingService.getAllBookings(pageRequest);

        model.addAttribute("bookings", bookingPage.getContent());
        model.addAttribute("currentPage", page); // current page
        model.addAttribute("totalPages", bookingPage.getTotalPages());

        model.addAttribute("param.status", "All");
        model.addAttribute("param.currentDate", LocalDate.now());
        model.addAttribute("param.currentTime", LocalTime.now().withSecond(0).withNano(0));



        return "bookingList";//keep the same view
    }

    // Search booking by userid
  /*  @GetMapping("/user/{userId}")
    public String getBookingsByUserId(@PathVariable String userId, Model model) {
        List<Booking> bookings = adminBookingService.getBookingsByUserId(userId);
        model.addAttribute("bookings", bookings);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        //return "booking_list_by_user";
        return "booking_list";
    }
*/
/*
    @GetMapping("/user/{userId}")
    public String getBookingsByUserId(@PathVariable String userId,
                                      @RequestParam(defaultValue = "1") int page,
                                      Model model) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Booking> bookingPage = adminBookingService.getBookingsByUserId(userId, pageable);

        model.addAttribute("bookings", bookingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookingPage.getTotalPages());

        model.addAttribute("param.status", "All");
        model.addAttribute("param.currentDate", LocalDate.now());
        model.addAttribute("param.currentTime", LocalTime.now().withSecond(0).withNano(0));



        model.addAttribute("searchUserId", userId);


        return "booking_list";
    }
*/

    @GetMapping("/user/search")
    public String getBookingsByUserIdForm(@RequestParam String userId,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentDate,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "HH:mm:ss") LocalTime currentTime,
                                          @RequestParam(defaultValue = "1") int page,
                                          Model model) {

        // set default time to prevent null
        if (currentDate == null) currentDate = LocalDate.now();
        if (currentTime == null) currentTime = LocalTime.now().withSecond(0).withNano(0);

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Booking> bookingPage = adminBookingService.getBookingsByUserId(userId, pageable);

        model.addAttribute("bookings", bookingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookingPage.getTotalPages());

        // parameter to support paging
        model.addAttribute("param.status", "All");
        model.addAttribute("param.currentDate", currentDate.toString());
        model.addAttribute("param.currentTime", currentTime.toString());
        model.addAttribute("searchUserId", userId);

        return "bookingList";
    }

    /*    @GetMapping("/id")
    public String getBookingById(@RequestParam Integer id, Model model) {
        Booking booking = adminBookingService.getBookingById(id);
        if (booking != null) {
            model.addAttribute("bookings", List.of(booking));
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
        } else {
            model.addAttribute("errorMessage", "Booking ID not found: " + id);
            model.addAttribute("currentPage", 1); // 如果没有找到也设置默认值
            model.addAttribute("totalPages", 1);
        }
        return "booking_list";

    }*/
    @GetMapping("/id")
    public String getBookingById(@RequestParam String id, Model model) {
        try {
            Integer bookingId = Integer.parseInt(id);
            Booking booking = adminBookingService.getBookingById(bookingId);
            if (booking != null) {
                model.addAttribute("bookings", List.of(booking));
            } else {
                model.addAttribute("errorMessage", "Booking ID not found: " + id);
            }
        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "Invalid Booking ID format: " + id);
        }

        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        return "bookingList";
    }

    // filter by booking status
/*    @GetMapping("/filter")
    public String getBookingsByStatus(@RequestParam String status,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentDate,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime currentTime,
                                      Model model) {
        List<Booking> bookings = adminBookingService.getBookingsByStatus(status, currentDate, currentTime);
        model.addAttribute("bookings", bookings);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        return "booking_list";
    }*/

    @GetMapping("/filter")
    public String getBookingsByStatus(@RequestParam String status,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentDate,
                                      @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime currentTime,
                                      @RequestParam(defaultValue = "1") int page,
                                      Model model) {

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Booking> bookingPage = adminBookingService.getBookingsByStatus(status, currentDate, currentTime, pageable);

        model.addAttribute("bookings", bookingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookingPage.getTotalPages());

        // add for paging
        model.addAttribute("param.status", status);
        model.addAttribute("param.currentDate", currentDate.toString());
        model.addAttribute("param.currentTime", currentTime.toString());

        return "bookingList";
    }

    // delete booking by id
    @PostMapping("/{id}")
    public String deleteBookingPost(@PathVariable Integer id) {
        adminBookingService.deleteBooking(id);
        return "redirect:/admin/booking";
    }

/*    @GetMapping("/admin/booking/filter")
    public String filterByDate(@RequestParam(required = false) String year,
                               @RequestParam(required = false) String month,
                               @RequestParam(required = false) String day,
                               Model model) {
        String dateString = "";

        if (year != null && !year.isEmpty()) {
            dateString += year;
            if (month != null && !month.isEmpty()) {
                dateString += "-" + month;
                if (day != null && !day.isEmpty()) {
                    dateString += "-" + day;
                }
            }
        }

        // use dateString to search(like "2025"、"2025-04"、"2025-04-15")

        return "adminBookingPage";
    }*/


}