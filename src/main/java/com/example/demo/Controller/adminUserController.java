package com.example.demo.Controller;

import com.example.demo.Repo.UserView;
import com.example.demo.Service.UserService;
import com.example.demo.Service.adminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class adminUserController {
    @Autowired
    private adminUserService adminUserService;

    // show all users
    @GetMapping
    public String showAllUsers(Model model) {
        List<UserView> users = adminUserService.getAllUsers();
        model.addAttribute("users", users);
        return "AdminUserManagement";
    }

    // fuzzy search
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<UserView> users = adminUserService.searchByKeyword(keyword);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "AdminUserManagement";
    }

    // filter search（status + bookingTimes）
    @GetMapping("/filter")
    public String filter(@RequestParam String status,
                         @RequestParam Integer gBookingTimes,
                         @RequestParam Integer lBookingTimes,
                         Model model) {
        if (gBookingTimes == null) {
            gBookingTimes = 0;
        }
        if (lBookingTimes == null) {
            lBookingTimes = Integer.MAX_VALUE;
        }
        List<UserView> users = adminUserService.filterUsers(status, gBookingTimes,lBookingTimes);
        model.addAttribute("users", users);
        model.addAttribute("status", status);
        model.addAttribute("gBookingTimes", gBookingTimes);
        model.addAttribute("lBookingTimes", lBookingTimes);
        return "AdminUserManagement";
    }

    // change the status of user
    @GetMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable("id") int id,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) Integer gBookingTimes,
                               @RequestParam(required = false) Integer lBookingTimes,
                               @RequestParam(required = false) String keyword,
                               Model model) {
        // change users' status
        adminUserService.toggleUserStatus(id);

        // set default value: if gBookingTimes null, then default 0，if lBookingTimes is null, default maximum value
        if (gBookingTimes == null) {
            gBookingTimes = 0;
        }
        if (lBookingTimes == null) {
            lBookingTimes = Integer.MAX_VALUE;
        }

        // get the updated users' information and keep the filter condition
        List<UserView> users;

        // filter logic
        if ("All".equals(status) || "Unlock".equals(status) || "Lock".equals(status)) {
            users = adminUserService.filterUsers(status, gBookingTimes, lBookingTimes);
            System.out.println("Hello"+ status + ", filter");
        }else if (keyword.length()>0) {
            users = adminUserService.searchByKeyword(keyword);
            System.out.println("Hello, search");
        }else {
            // no filter condition => get all user
            users = adminUserService.getAllUsers();
            System.out.println("Hello, World!");
        }

        // transfer the filter conditon and user list to html
        model.addAttribute("users", users);
        model.addAttribute("status", status);
        model.addAttribute("gBookingTimes", gBookingTimes);
        model.addAttribute("lBookingTimes", lBookingTimes);
        model.addAttribute("keyword", keyword);

        return "AdminUserManagement";
    }

    @GetMapping("/resetBookingTimes")
    public String resetBookingTimes(Model model) {
        // use service layer function
        adminUserService.setUserBookingTimesZero();

        // reload all user and transfer it into frontend
        List<UserView> users = adminUserService.getAllUsers();
        model.addAttribute("users", users);

        return "AdminUserManagement";
    }

    @GetMapping("/resetBookingTimesAndUnlockAll")
    public String resetBookingTimesAndUnlockAll(Model model) {
        // Reset booking times for all users
        adminUserService.setUserBookingTimesZero();

        // Unlock all locked users
        adminUserService.unlockAllLockedUsers();

        // Reload the list of users and pass it to the view
        List<UserView> users = adminUserService.getAllUsers();
        model.addAttribute("users", users);

        // Return to the user management page
        return "AdminUserManagement";
    }



}
