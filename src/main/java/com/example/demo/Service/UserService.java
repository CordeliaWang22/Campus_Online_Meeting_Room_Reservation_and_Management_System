package com.example.demo.Service;

import com.example.demo.Repo.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface UserService {
    public Optional<User> getUserById(Integer id);
    public List<User> getAllUsers();

    String register(User user);

    String sendVerificationCode(Integer id, String email);

    String checkVerificationCode(User user, String code);

    Map<String, Object> verifyCodeAndGenerateToken(Integer id, String email, String code);

    String setPassword(Integer id, String email, String password1, String password2);

    User getUserInfo(String email);


    public String updateInfo(User user, Integer id, String email, String name, String password, String newpword1, String newpword2, String phoneNum);
    String uploadAvatar( Integer UserId, MultipartFile file);

    void SaveNewUser(User user);
    public void init();
    @Transactional
    void updateUserPassword(Integer UserId, String password);

    @Transactional
    void updateUserAvatar(Integer UserId,String avatar);

    @Transactional
    void updateUserName(Integer UserId,String name);

    @Transactional
    void updateUserBookingTimes(Integer UserId,int times);
}
