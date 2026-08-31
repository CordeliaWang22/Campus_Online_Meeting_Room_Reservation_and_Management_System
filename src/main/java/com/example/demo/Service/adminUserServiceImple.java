package com.example.demo.Service;


import com.example.demo.Repo.AdminUserRepo;
import com.example.demo.Repo.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class adminUserServiceImple implements adminUserService{
    @Autowired
    private AdminUserRepo adminUserRepo;

    @Override
    public List<UserView> getAllUsers() {
        return adminUserRepo.findAllWithoutPassword();
    }

    @Override
    public void toggleUserStatus(int userId) {
        int updated = adminUserRepo.toggleUserStatusById(userId);
        if (updated == 0) {
            System.out.println("No user found or status unchanged for ID: " + userId);
        } else {
            System.out.println("Toggled status for user ID: " + userId);
        }
    }

    @Override
    public List<UserView> filterUsers(String status, Integer gBookingTimes, Integer lBookingTimes) {
        // set default value
        if (gBookingTimes == null) {
            gBookingTimes = 0;
        }
        if (lBookingTimes == null) {
            lBookingTimes = Integer.MAX_VALUE;
        }

        // throw exception
        if (gBookingTimes > lBookingTimes) {
            List<UserView> a = new ArrayList<UserView>();
            return a;
        }

        // filter by status
        if ("All".equals(status)) {
            return adminUserRepo.findByBookingTimesGreaterThanEqualAndBookingTimesLessThanEqual(gBookingTimes, lBookingTimes);
        } else {
            return adminUserRepo.findByStatusAndBookingTimesGreaterThanEqualAndBookingTimesLessThanEqual(status, gBookingTimes, lBookingTimes);
        }
    }

    @Override
    public List<UserView> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return adminUserRepo.findAllWithoutPassword();
        }
        return adminUserRepo.searchByKeywordAcrossFields(keyword);
    }

    @Override
    public void setUserBookingTimesZero(){
       adminUserRepo.setAllBookingTimesToZero();
    }

    @Override
    public void unlockAllLockedUsers() {
        // Fetch users with 'Lock' status
        List<UserView> lockedUsers = adminUserRepo.findByStatus("Lock");

        // Update the status of all locked users to 'Unlock'
        for (UserView user : lockedUsers) {
            toggleUserStatus(user.getId());
        }

    }

}
