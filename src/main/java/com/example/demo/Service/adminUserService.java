package com.example.demo.Service;

import com.example.demo.Repo.UserView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface adminUserService {
    List<UserView> getAllUsers();

    void toggleUserStatus(int userId);

    List<UserView> filterUsers(String status, Integer gBookingTimes, Integer lBookingTimes);

    List<UserView> searchByKeyword(String keyword);

    void setUserBookingTimesZero();

    void unlockAllLockedUsers();
}
