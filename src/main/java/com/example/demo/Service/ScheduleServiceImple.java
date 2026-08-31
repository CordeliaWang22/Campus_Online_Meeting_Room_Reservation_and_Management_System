package com.example.demo.Service;

import com.example.demo.Repo.Room;
import com.example.demo.Repo.RoomRepo;
import com.example.demo.Repo.Schedule;
import com.example.demo.Repo.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImple implements ScheduleService {
    @Autowired
    ScheduleRepo scheduleRepo;
    @Autowired
    RoomRepo roomRepo;

    @Override
    public Set<Room> findRoomByTime(LocalTime start, LocalTime end) {


        LocalDate today = LocalDate.now();


        List<Schedule> allSchedules = scheduleRepo.findAll();

        // Collect all room IDs with time conflicts
        Set<String> conflictRoomIds = allSchedules.stream()
                .filter(schedule -> schedule.getStatus() == true) // Consider only valid reservations
                .filter(schedule -> {
                    LocalDate scheduleDate = LocalDate.parse(schedule.getDate());
                    //Consider only today or future dates
                    return !scheduleDate.isBefore(today) &&
                            schedule.getStartTime().isBefore(end) && // Check for time slot overlap
                            start.isBefore(schedule.getEndTime());
                })
                .map(Schedule::getRoomId)
                .collect(Collectors.toSet());

        // Check all rooms, excluding the ones with conflicts
        return roomRepo.findAll().stream()
                .filter(room -> !conflictRoomIds.contains(room.getId()))
                .collect(Collectors.toSet());
    }


    @Override
    public Set<Room> findRoomByDate(String date) {
        LocalDate inputDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        // Check if the date is between today and the next 7 days (inclusive of today and the seventh day
        if (inputDate.isBefore(today) || inputDate.isAfter(sevenDaysLater)) {
            return null;
        }
        // Get all schedules for today
        List<Schedule> schedulesOnDate = scheduleRepo.findByDate(date);
        if (schedulesOnDate.isEmpty()) {
            // No reservations have been made, all rooms are available
            return new HashSet<>(roomRepo.findAll());
        }

// Find the rooms that have already been booked on that date
        Set<String> reservedRoomIds = schedulesOnDate.stream()
                .map(Schedule::getRoomId)
                .collect(Collectors.toSet());

// Query all rooms and exclude the ones that are already booked
        List<Room> allRooms = roomRepo.findAll();
        Set<Room> availableRooms = allRooms.stream()
                .filter(room -> !reservedRoomIds.contains(room.getId()))
                .collect(Collectors.toSet());

        return availableRooms;

    }

    @Override
    public Set<Room> findRoomByDateAndTime(String date, LocalTime start, LocalTime end) {
        LocalDate inputDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);


        if (inputDate.isBefore(today) || inputDate.isAfter(sevenDaysLater)) {
            return null;
        }


        List<Schedule> schedulesOnDate = scheduleRepo.findByDate(date);
        if (schedulesOnDate.isEmpty()) {
            return new HashSet<>(roomRepo.findAll());
        }
        Set<String> conflictRoomIds = new HashSet<>();
        for (Schedule schedule : schedulesOnDate) {
            // Time overlap condition: start1 < end2 && start2 < end
            if (schedule.getStartTime().isBefore(end) && start.isBefore(schedule.getEndTime())) {
                conflictRoomIds.add(schedule.getRoomId());
            }
        }

        List<Room> allRooms = roomRepo.findAll();
        Set<Room> availableRooms = new HashSet<>();
        for (Room room : allRooms) {
            if (!conflictRoomIds.contains(room.getId())) {
                availableRooms.add(room);
            }
        }

        return availableRooms;

    }


    @Override
    public Schedule addOrUpdateSchedule(Schedule schedule) {

        Optional<Schedule> existingSchedule = scheduleRepo.findByRoomIdAndDateAndStartTimeAndEndTime(
                schedule.getRoomId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );

        if (existingSchedule.isPresent()) {
            Schedule s = existingSchedule.get();
            s.setStatus(schedule.getStatus()); // update status
            return scheduleRepo.save(s);
        } else {
            return scheduleRepo.save(schedule);
        }
    }

    @Override
    public List<Schedule> getByRoomAndDate(String roomId, String date) {

        return scheduleRepo.findByRoomIdAndDate(roomId, date);

    }


}
