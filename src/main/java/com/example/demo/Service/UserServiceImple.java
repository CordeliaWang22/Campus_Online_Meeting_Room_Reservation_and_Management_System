package com.example.demo.Service;

import com.example.demo.Repo.StudentRepo;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Repo.User;
import com.example.demo.util.MailService;
import com.example.demo.util.VerificationCodeCache;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.mail.MailSendException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImple implements UserService {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private MailService mailService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${avatar.upload.dir}")
    private String uploadDir;

    @Value("${avatar.default.filename}")
    private String defaultAvatarFilename;
    @Override
    public Optional<User> getUserById(Integer id) {

        return userRepository.findByIdEquals(id);

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public String register(User user) {


        // Check if the ID exists in the student table
        if (user.getId() == null || !studentRepo.existsById(user.getId())) {
            System.out.println("invalid");
            return "The ID does not exist.";
        }

        // Email validity
        if (!isValidEmail(user.getEmail())) {
            return "Invalid email address.";
        }
        //Email has already been registered
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "The email has been registered.";
        }
        VerificationCodeCache.removeCode(user.getEmail());
        return "Valid";
    }

    public String checkVerificationCode(User user, String code) {
        if (!VerificationCodeCache.verify(user.getEmail(), code)) {
            return "Verification code incorrect or expired.";
        }
        return "Verification code is correct";
    }

    @Override
    public Map<String, Object> verifyCodeAndGenerateToken(Integer id, String email, String code) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);

        String result = checkVerificationCode(user, code);

        if ("Verification code is correct".equals(result)) {
            String token = UUID.randomUUID().toString();
            String userData = id + ":" + email;
            redisTemplate.opsForValue().set("verified_user:" + token, userData, Duration.ofMinutes(10));
            return Map.of("code", 0, "msg", result, "token", token);
        } else {
            return Map.of("code", 1, "msg", result);
        }
    }



    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    @Override
    public String sendVerificationCode(Integer id, String email) {
        if (!studentRepo.existsById(id)) {
            return "The ID does not exist.";
        }
        if (!isValidEmail(email)) {
            return "Invalid email address.";
        }

        try {
            return mailService.sendCodeToEmail(email);
        } catch (MailSendException e) {
            // Exception thrown by the email service layer
            return "Unable to send email. Please check if the email address is correct.";
        } catch (Exception e) {
            // other exception
            return "A system error occurred while sending the verification code.";
        }
    }


    @Override
    public String setPassword(Integer id, String email, String password1, String password2) {
        if (!password1.equals(password2)) {
            return "Entered passwords differ!";
        }
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password1));
        user.setStatus("unlock");
        userRepository.save(user);
        return "Register successfully";
    }

    @Override
    public User getUserInfo(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }


    @Override
    public String updateInfo(User user, Integer id, String email, String name, String password, String newpword1, String newpword2, String phoneNum) {
        user = getUserById(id).get();

        // Update non-password information
        if (email != null) user.setEmail(email);
        if (name != null) user.setName(name);
        if (phoneNum != null) user.setPhoneNum(phoneNum);

        // Handle password update
        String passwordUpdateResult = "update successful";
        if (password != null && newpword1 != null && newpword2 != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                if (newpword1.equals(newpword2)) {
                    user.setPassword(passwordEncoder.encode(newpword1));
                } else {
                    passwordUpdateResult = "The new passwords entered must match.";
                }
            } else {
                passwordUpdateResult = "The original password is incorrect.";
            }
        }

        SaveNewUser(user);
        return passwordUpdateResult;
    }

    @PostConstruct
    public void init() {
        // Create upload directory
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Ensure the default avatar exists
        File defaultAvatar = new File(uploadDir, defaultAvatarFilename);
        if (!defaultAvatar.exists()) {
            try {
                Resource resource = new ClassPathResource("static/images/" + defaultAvatarFilename);
                Files.copy(resource.getInputStream(), defaultAvatar.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateUserPassword(Integer UserId, String password) {
        userRepository.updatePasswordById(UserId, password);
    }

    @Override
    public void updateUserAvatar(Integer UserId, String avatar) {
        userRepository.updateAvatarById(UserId,avatar);
    }

    @Override
    public void updateUserName(Integer UserId, String name) {
        userRepository.updateNameById(UserId,name);

    }

    @Override
    public void updateUserBookingTimes(Integer UserId, int times) {
        userRepository.updateBookingTimesById(UserId,times);
    }

    @Override
    public String uploadAvatar(Integer userId, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return null;
            }

            // Generate file name
            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String fileName = userId + "_" + System.currentTimeMillis() + extension;

            // Save to external directory
            Path targetPath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Update user avatar information
            Optional<User> userOpt = getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Delete the old avatar file (if it exists and is not the default avatar)
                if (user.getAvatar() != null && !user.getAvatar().equals(defaultAvatarFilename)) {
                    File oldAvatar = new File(uploadDir, user.getAvatar());
                    if (oldAvatar.exists()) {
                        oldAvatar.delete();
                    }
                }
                user.setAvatar(fileName);
                userRepository.save(user);
                System.out.println(fileName);
                return fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void SaveNewUser(User user) {
        User oldUser = getUserById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getName() != null) oldUser.setName(user.getName());
        if (user.getEmail() != null) oldUser.setEmail(user.getEmail());
        if (user.getPassword() != null) oldUser.setPassword(user.getPassword());
        if (user.getPhoneNum() != null) oldUser.setPhoneNum(user.getPhoneNum());

        userRepository.save(oldUser);
    }

}
