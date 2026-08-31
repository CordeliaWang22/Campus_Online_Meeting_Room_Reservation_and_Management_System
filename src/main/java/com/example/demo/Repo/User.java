package com.example.demo.Repo;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`index`") // index is a reserved keyword in SQL, it should be enclosed in backticks
    private Integer index;

    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    private String password = "123456haha";

    private String name;

    @Column(name = "phone_num")
    private String phoneNum;

    private String avatar ;

    @Column(name = "booking_times")
    private int bookingTimes;

    @NotNull
    private String status = "Unlock";

    // === Getters and Setters ===

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getBookingTimes() {
        return bookingTimes;
    }

    public void setBookingTimes(int bookingTimes) {
        this.bookingTimes = bookingTimes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @Transient
    public String getAvatarUrl() {
        if (avatar == null || avatar.isEmpty()) {
            return "/images/default-avatar.jpg"; // default avatar
        }
        if (avatar.startsWith("/")) {
            return avatar; // It is already a complete path
        }
        return "/avatars/" + avatar; // Path of the uploaded avatar
    }
}
