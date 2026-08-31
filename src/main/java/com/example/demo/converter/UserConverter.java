package com.example.demo.converter;
import com.example.demo.Repo.User;
import com.example.demo.dto.UserDTO;

public class UserConverter {
    //Convert an object of type user to an object of type UserDTO
    public static UserDTO convertUser(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setIndex(user.getIndex());
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setPhoneNum(user.getPhoneNum());
        return userDTO;
    }
    //Convert an object of type UserDTO to an object of type User
    public static User convertUser(UserDTO userDTO){
        User user = new User();
        user.setIndex(userDTO.getIndex());
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNum(userDTO.getPhoneNum());
        return user;
    }
}
