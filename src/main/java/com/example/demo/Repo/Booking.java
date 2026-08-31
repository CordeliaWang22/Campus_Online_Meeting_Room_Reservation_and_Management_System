package com.example.demo.Repo;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;


@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_time")
    @NotNull
    private String startTime;

    @Column(name = "end_time")
    @NotNull
    private String endTime;

    @NotNull
    private String date;

    @NotNull
    private String reason;

    @Column(name = "room_id")
    @NotNull
    private String roomId;

    @Column(name = "user_id")
    @NotNull
    private String userId;

    public Booking() {
    }

    public Booking(String startTime, String endTime, String date, String reason, String roomId, String userId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.reason = reason;
        this.roomId = roomId;
        this.userId = userId;
    }


    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}