package com.example.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendCodeToEmail(String email) {
        String code = String.format("%06d", new Random().nextInt(999999)); // 始终 6 位

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dretty<3221056913@qq.com>");
        message.setTo(email);
        message.setSubject("【注册验证码】");
        message.setText("Your verification code is: " + code + ". It is valid for 5 minutes. Please do not share it.");

        mailSender.send(message);
        VerificationCodeCache.saveCode(email, code);
        return code;
    }
}

