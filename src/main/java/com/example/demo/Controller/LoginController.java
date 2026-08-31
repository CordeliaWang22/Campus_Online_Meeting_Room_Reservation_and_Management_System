package com.example.demo.Controller;



import com.example.demo.Repo.Student;
import com.example.demo.Repo.User;
import com.example.demo.Service.StudentService;
import com.example.demo.Service.StudentServiceImple;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @PostMapping("/login")
    public String handleLogin(@ModelAttribute User user, Model model, HttpSession session) {
        Optional<Student> studentDbs = studentService.findStudentById(user.getId());
        if (!studentDbs.isPresent()) {
            model.addAttribute("error", "ID does not exist");
            return "Login"; // If the user is not logged in, redirect to the login page.
        }

        Optional<User> userDbs = userService.getUserById(user.getId());

        if (userDbs.isPresent() &&
                userDbs.get().getId().equals(user.getId()) &&
                passwordEncoder.matches(user.getPassword(), userDbs.get().getPassword())) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/welcome"; // Login successful, redirect to the welcome page.
        } else {
            model.addAttribute("error", "Incorrect password");
            return "Login";
        }
    }

    // Forgot password, send verification code.
    @PostMapping("/forgotPassword/sendCode")
    public String sendCode(@RequestParam Integer id,
                           @RequestParam String email,
                           Model model) throws UnsupportedEncodingException {


        User user = userService.getUserById(id).get();
        System.out.println("getted:"+user.getEmail());
        System.out.println("email:"+email);
        if (!user.getEmail().equals(email)) {
            String encodedError = URLEncoder.encode("Entered email does not match the original email.", StandardCharsets.UTF_8.toString());
            return "redirect:/login?showForgot=true&id=" + id + "&email=" + email + "&error=" + encodedError;
        }

        String msg = userService.sendVerificationCode(id, email);


        model.addAttribute("id", id);
        model.addAttribute("email", email);
        System.out.println("msg:"+msg);
        if (msg!="The ID does not exist." && msg !="Invalid email address.") {

            model.addAttribute("info", "The verification code has been sent, please check your email!");
            model.addAttribute("isReset",true);


        } else {
            model.addAttribute("error", msg);
        }

        return  "redirect:/forgotPassword/verify?id=" + id + "&email=" + email + "&isReset=true";
    }

    @GetMapping("/forgotPassword/verify")
    public String showVerifyPage(@RequestParam(required = false) Integer id,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) Boolean isForgotPassword,
                                 Model model) {

        if (id != null) {
            model.addAttribute("id", id);
        }
        if (email != null) {
            model.addAttribute("email", email);
        }

        // Set a flag to indicate that this is the password recovery process.
        model.addAttribute("isForgotPassword", isForgotPassword != null ? isForgotPassword : true);


        if (model.getAttribute("error") != null) {
            model.addAttribute("error", model.getAttribute("error"));
        }


        if (model.getAttribute("info") != null) {
            model.addAttribute("info", model.getAttribute("info"));
        }

        System.out.println("Showing verify page for ID: " + id + ", Email: " + email);

        return "VerifyPage";
    }

    @PostMapping("/forgotPassword/verify")
    public String verifyResetPasswordCode(@RequestParam Integer id,
                                          @RequestParam String email,
                                          @RequestParam String code,
                                          Model model) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        String msg = userService.checkVerificationCode(user, code);

        if ("Verification code is correct".equals(msg)) {
            // Modify here to ensure redirection to the correct path and pass the isReset parameter
            return "redirect:/SetNewPassword?id=" + id + "&email=" + email + "&isReset=true";
        } else {
            model.addAttribute("error", msg);
            model.addAttribute("isForgotPassword", true);
            model.addAttribute("email", email);
            model.addAttribute("id", id);
            return "VerifyPage";
        }
    }



    @GetMapping("/SetNewPassword")
    public String showSetPasswordPage(@RequestParam(required = false) Integer id,
                                      @RequestParam(required = false) String email,
                                      @RequestParam(required = false) Boolean isReset,
                                      Model model) {
        model.addAttribute("id", id);
        model.addAttribute("email", email);
        model.addAttribute("isReset", isReset);
        return "setPassword";
    }
    @PostMapping("/SetNewPassword")
    public String setNewPass(
            Model model,
            @ModelAttribute User user,
            @RequestParam String email,
            @RequestParam String password1,
            @RequestParam String password2

    ) {
        System.out.println("reset");
        User userDbs = userService.getUserInfo(email);
        if (password1.equals(password2) && password1!=null && password2!=null) {

            userService.updateUserPassword(userDbs.getId(), passwordEncoder.encode(password1));

            model.addAttribute("success", "Password reset successful");
            model.addAttribute("isReset", true);
        } else if (password1 == null) {
            model.addAttribute("error", "Please enter a new password.");
        }
        else if (password2 == null) {
            model.addAttribute("error","Please enter the confirmation password." );
        }
        else model.addAttribute("error", "The two passwords do not match.");
        return "redirect:/login?success=Password set successfully";

    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        // Clear user information from the session
        session.removeAttribute("loggedInUser");
        // Invalidate the session
        session.invalidate();
        return "redirect:/login";
    }
}