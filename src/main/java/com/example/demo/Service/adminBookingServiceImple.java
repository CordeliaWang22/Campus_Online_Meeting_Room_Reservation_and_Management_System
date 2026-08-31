package com.example.demo.Service;

import com.example.demo.Repo.AdminBookingRepo;
import com.example.demo.Repo.Booking;
import com.example.demo.Repo.BookingRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Service
public class adminBookingServiceImple implements adminBookingService {

    @Autowired
    private AdminBookingRepo adminBookingRepo;

    // get all booking in datbase
    @Override
    public List<Booking> getAllBookings() {
        return adminBookingRepo.findAll();
    }

    // search booking based on id
    /*@Override
    public Booking getBookingById(Integer id) {
        return adminBookingRepo.findById(id).orElseThrow(null);
    }*/
    @Override
    public Booking getBookingById(Integer id) {
        return adminBookingRepo.findById(id).orElse(null); // safe
    }



    // search booking based on id
    @Override
    public List<Booking> getBookingsByUserId(String userId) {
        return adminBookingRepo.findByUserId(userId);
    }
    //added
    @Override
    public Page<Booking> getBookingsByUserId(String userId, Pageable pageable) {
        List<Booking> filtered = adminBookingRepo.findByUserId(userId);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Booking> paged = filtered.subList(start, end);
        return new PageImpl<>(paged, pageable, filtered.size());
    }


    //filter booking based on status：All/Processing/Finished
/*    @Override
    public List<Booking> getBookingsByStatus(String status, LocalDate currentDate, LocalTime currentTime) {
        if ("All".equalsIgnoreCase(status)) {
            return getAllBookings();
        } else if ("Finished".equalsIgnoreCase(status)) {
            return adminBookingRepo.findFinishedBookings(currentDate, currentTime);
        } else if ("Processing".equalsIgnoreCase(status)) {
            return adminBookingRepo.findProcessingBookings(currentDate, currentTime);
        }
        return null;
    }*/
    @Override
    public List<Booking> getBookingsByStatus(String status, LocalDate currentDate, LocalTime currentTime) {
        List<Booking> allBookings = adminBookingRepo.findAll();

        return allBookings.stream()
                .filter(booking -> {
                    try {
                        LocalDate bookingDate = LocalDate.parse(booking.getDate());
                        LocalTime bookingEndTime = LocalTime.parse(booking.getEndTime());


                        if ("Finished".equalsIgnoreCase(status)) {
                            return bookingDate.isBefore(currentDate)
                                    || (bookingDate.equals(currentDate) && bookingEndTime.isBefore(currentTime));
                        } else if ("Processing".equalsIgnoreCase(status)) {
                            return bookingDate.isAfter(currentDate)
                                    || (bookingDate.equals(currentDate) && !bookingEndTime.isBefore(currentTime));
                        } else {
                            return true; // status = "All"
                        }
                    } catch (Exception e) {
                        // if error then skip
                        return false;
                    }
                })
                .toList();
    }
    @Override
    public Page<Booking> getBookingsByStatus(String status, LocalDate currentDate, LocalTime currentTime, Pageable pageable) {
        List<Booking> all = adminBookingRepo.findAll();

        List<Booking> filtered = all.stream()
                .filter(booking -> {
                    try {
                        LocalDate date = LocalDate.parse(booking.getDate());
                        LocalTime end = LocalTime.parse(booking.getEndTime());

                        if ("Finished".equalsIgnoreCase(status)) {
                            return date.isBefore(currentDate) ||
                                    (date.equals(currentDate) && end.isBefore(currentTime));
                        } else if ("Processing".equalsIgnoreCase(status)) {
                            return date.isAfter(currentDate) ||
                                    (date.equals(currentDate) && !end.isBefore(currentTime));
                        } else {
                            return true;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Booking> paged = filtered.subList(start, end);

        return new PageImpl<>(paged, pageable, filtered.size());
    }

    /*
    // save new bboking to databse
    @Override
    public void addNewBookings(Booking newBooking) {
        bookingRepo.save(newBooking);
    }*/

    // delete booking based on id
    @Transactional
    @Override
    public void deleteBooking(Integer id) {
        adminBookingRepo.deleteById(id);
    }

    @Override
    public Page<Booking> getAllBookings(Pageable pageable) {
        return adminBookingRepo.findAll(pageable);
    }
}
