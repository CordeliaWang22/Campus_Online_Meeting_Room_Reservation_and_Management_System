package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class AvatarConfig {
    @Value("${avatar.upload.dir}")
    private String uploadDir;

    @Value("${avatar.default.path}")
    private String defaultPath;

    @PostConstruct
    public void init() {
        try {

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }


            File defaultAvatar = new File(defaultPath);
            if (!defaultAvatar.exists()) {
                defaultAvatar.getParentFile().mkdirs();
                Resource resource = new ClassPathResource("static/images/default-avatar.jpg");
                Files.copy(resource.getInputStream(), defaultAvatar.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}