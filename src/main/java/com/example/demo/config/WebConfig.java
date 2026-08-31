package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${room.upload.dir}")
    private String roomUploadDir;

    @Value("${avatar.upload.dir}")
    private String avatarUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure the access path for room images
        registry.addResourceHandler("/rooms/**")
                .addResourceLocations("file:" + roomUploadDir + "/");

        // Configure the access path for avatars
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + avatarUploadDir + "/");
    }}