package com.example.demo.Repo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule,Integer> {
    public List<Schedule> findByDate(String date);



    @Query("SELECT s FROM Schedule s WHERE s.roomId = :roomId AND s.date = :date AND s.startTime IN :startTimes")
    List<Schedule> findByRoomIdAndDateAndStartTimes(@Param("roomId") String roomId,
                                                    @Param("date") String date,
                                                    @Param("startTimes") List<LocalTime> startTimes);
    Optional<Schedule> findByRoomIdAndStartTimeAndDate(String roomId, LocalTime startTime, String date);




    Optional<Schedule> findByRoomIdAndDateAndStartTimeAndEndTime(
            String roomId,
            String date,
            LocalTime startTime,
            LocalTime endTime
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.roomId = :roomId AND s.date = :date AND s.startTime IN :startTimes")
    int deleteByRoomIdAndDateAndStartTimeIn(@Param("roomId") String roomId,
                                            @Param("date") String date,
                                            @Param("startTimes") List<LocalTime> startTimes);

    List<Schedule> findByRoomIdAndDate(String roomId, String dateStr);


}
