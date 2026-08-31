package com.example.demo.Repo;

import jakarta.persistence.*;

import java.time.LocalTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "date")
    private String date;

    @Column(name = "status")
    private boolean status;

    public Schedule(LocalTime start, LocalTime end, String date, String roomId, boolean b) {
        this.startTime = start;
        this.endTime = end;
        this.date = date;
        this.roomId = roomId;
        this.status = b;
    }

    public Schedule(LocalTime startTime, String date, String roomId, boolean status) {

        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(30);
        this.date = date;
        this.roomId = roomId;
        this.status = status;
    }

    public Schedule() {

    }


    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
