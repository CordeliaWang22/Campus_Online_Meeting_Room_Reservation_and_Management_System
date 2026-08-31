package com.example.demo.Service;

import com.example.demo.Repo.Room;
import com.example.demo.Repo.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public interface ScheduleService {

    public Set<Room> findRoomByTime(LocalTime start, LocalTime end);
    public Set<Room> findRoomByDate(String date);
    public Set<Room> findRoomByDateAndTime(String date, LocalTime start, LocalTime end);
//    public List<Schedule> getByRoomIdAndDateAndStartTimes(String roomId, String date, List<LocalTime> startTimes );



    Schedule addOrUpdateSchedule(Schedule schedule);
    List<Schedule> getByRoomAndDate(String roomId, String date);

}
