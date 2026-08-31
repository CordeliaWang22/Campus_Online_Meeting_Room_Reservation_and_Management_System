package com.example.demo.dto;

public class UserDTO {
    private Integer index;
    private Integer id;
    private String email;
    private String password;
    private String phoneNum;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getPhoneNum() { return phoneNum; }

    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

}
