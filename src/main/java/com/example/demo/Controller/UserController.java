package com.example.demo.Controller;

import com.example.demo.Repo.User;
import com.example.demo.Repo.UserRepo;
import com.example.demo.dto.UserDTO;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    // Display Registration Page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());

        return "Register"; // register.html
    }

    // Display Verification Page
    @GetMapping("/VerifyPage")
    public String showVerifyPage(@RequestParam("id") Integer id,
                                 @RequestParam("email") String email,
                                 @RequestParam(required = false) String type,
                                 Model model) {
        model.addAttribute("id", id);
        model.addAttribute("email", email);
        model.addAttribute("type", "register");
        return "VerifyPage";
    }

    // Display Set Password Page
    @GetMapping("/setPasswordPage")
    public String showSetPasswordPage(@RequestParam("id") Integer id,
                                      @RequestParam("email") String email,
                                      @RequestParam(required = false) Boolean isReset,
                                      Model model) {
        System.out.println("Entering setPasswordPage with id: " + id + ", email: " + email + ", isReset: " + isReset);
        model.addAttribute("id", id);
        model.addAttribute("email", email);
        model.addAttribute("isReset", isReset);

        return "setPassword";
    }

    @GetMapping("/login")
    public String showLoginpage(@RequestParam(required = false) Integer id, Model model) {
        User user = new User();

        if (id != null) {
            user.setId(id);
        }

        model.addAttribute("user", user);
        return "Login";
    }


    // Edit Personal Information Page
    @GetMapping("/updateInfoPage")
    public String showUpdateInfoPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "updateInfo";
    }


    // Verify ID and Email
    @PostMapping("/register")
    public String register(@RequestParam String id,
                           @RequestParam String email,
                           Model model

    ) {

        User user = new User();

        for (int i = 0; i < id.length(); i++) {
            char ch = id.charAt(i);
            System.out.println("enter");
            if (ch < '0' || ch > '9') {
                model.addAttribute("error", "ID should contain only numbers");
                return "Register";
            }
        }

        // The ID is all digits, convert it to Integer
        Integer parsedId = Integer.parseInt(id);

        Optional<User> duplicateUser = userService.getUserById(parsedId);

        if (duplicateUser.isPresent()) {
            model.addAttribute("error", "ID has already been registered");
            System.out.println("id already exist");
            return "Register";
        }

        user.setId(parsedId);


        user.setEmail(email);

        String msg = userService.register(user);


        if (msg.contains("Valid")) {

            return "redirect:/VerifyPage?id=" + id + "&email=" + email + "&type=register";
        } else {

            model.addAttribute("error", msg);
            return "Register";
        }
    }

    //Send verification code after the frontend returns the email.
    @PostMapping("/sendCode")
    public String sendCode(@RequestParam Integer id,
                           @RequestParam String email,
//                           'type' parameter is used to distinguish whether
//                            the verification code is for password recovery
//                            or registration verification
                           @RequestParam(required = false) String type,
                           Model model) {
        String msg = userService.sendVerificationCode(id, email);

        model.addAttribute("id", id);
        model.addAttribute("email", email);
        System.out.println("msg:" + msg);
        if (msg != "The ID does not exist." && msg != "Invalid email address.") {
            model.addAttribute("info", "The verification code has been sent, please check your email!");
        } else if (msg.equals("Unable to send email. Please check if the email address is correct.")) {
            model.addAttribute("error", msg);
        }


        return "redirect:/VerifyPage?id=" + id + "&email=" + email + "&type=" + type;
    }

    @PostMapping("/VerifyPage")
    public String verifyCode(@RequestParam Integer id,
                             @RequestParam String email,
                             @RequestParam String code,
                             @RequestParam String type,
                             Model model) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        String msg = userService.checkVerificationCode(user, code);
        System.out.println(msg);
        model.addAttribute("id", id);
        model.addAttribute("email", email);

        if ("Verification code is correct".equals(msg)) {
            System.out.println("verify ok");

            return "redirect:/setPasswordPage?id=" + id + "&email=" + email + "&isReset=false&type=" + type;
        } else {
            model.addAttribute("error", msg);
            return "VerifyPage";
        }
    }


    // set password
    @PostMapping("/setPasswordPage")
    public String setPassword(@RequestParam Integer id,
                              @RequestParam String email,
                              @RequestParam String password1,
                              @RequestParam String password2,
                              @RequestParam(required = false) String type,
                              Model model) {
        System.out.println("enter usercontroller setPassword");
        String result = userService.setPassword(id, email, password1, password2);
        if ("Register successfully".equals(result)) {
            if ("register".equals(type)) {
                return "redirect:/login?id=" + id + "&success=Registration completed successfully";
            }
        }
        model.addAttribute("id", id);
        model.addAttribute("email", email);
        model.addAttribute("error", result);
        return "redirect:/setPasswordPage";
    }

    // Get User Information
    @GetMapping("/welcome/info")
//    @ResponseBody
    public String userInfo(Model model, HttpSession httpSession) {

        User loggeduser = (User) httpSession.getAttribute("loggedInUser");
        if (loggeduser == null) {
            return "redirect:/login"; // If the user is not logged in, redirect to the login page
        }

        Optional<User> userDbs = userService.getUserById(loggeduser.getId());

        User user = userDbs.get();
        System.out.println("ava:" + user.getAvatar());
        if (user == null) {
            model.addAttribute("user", user);
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "userInfo";
    }

    @PostMapping("/welcome/info")
    @ResponseBody
    public Map<String, String> update(@RequestParam(required = false) String email,
                                      @RequestParam(required = false) String name,
                                      @RequestParam(required = false) String password,
                                      @RequestParam(required = false) String newpword1,
                                      @RequestParam(required = false) String newpword2,
                                      @RequestParam(required = false) String phoneNum,
                                      Model model, HttpSession httpSession) {
        System.out.println("update");
        Map<String, String> response = new HashMap<>();
        User loggeduser = (User) httpSession.getAttribute("loggedInUser");
        if (loggeduser == null) {
            response.put("error", "Please login first!");
            return response;
        }
        User user = userService.getUserById(loggeduser.getId()).get();
        String msg = userService.updateInfo(
                user, user.getId(), email, name,
                password, newpword1, newpword2, phoneNum);
        System.out.println(msg);
        model.addAttribute("user", user);
        if (msg.equals("update successful")) {
            response.put("success", "Update successfully!");
        } else {

            response.put("error", msg);
        }
        return response;
    }


    @PostMapping("/userInfo/upload")
    public String upload(HttpSession httpSession,
                         @RequestParam("file") MultipartFile file,
                         Model model) {
        User loggeduser = (User) httpSession.getAttribute("loggedInUser");
        if (loggeduser == null) {
            model.addAttribute("error", "Please login first!");
            return "redirect:/login";
        }

        String fileName = userService.uploadAvatar(loggeduser.getId(), file);
        if (fileName != null) {
            User user = userService.getUserById(loggeduser.getId()).get();
            model.addAttribute("user", user);
            model.addAttribute("success", "Upload successfully!");
        } else {
            model.addAttribute("error", "Upload failed!");
        }
        return "userInfo";
    }
}
