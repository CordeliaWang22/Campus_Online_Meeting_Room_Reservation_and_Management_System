package com.example.demo.Controller;


import com.example.demo.Repo.Schedule;
import com.example.demo.Service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public List<Schedule> getScheduleByRoomAndDate(@RequestParam String roomId,
                                                   @RequestParam String date) {
        return scheduleService.getByRoomAndDate(roomId, date);
    }
}
