package com.example.govcentralappointmentbooking.models;

import androidx.annotation.NonNull;

public class User {

    public String userName;
    public String email;
    public String phone;

    public User() {}

    public User(String userName, String email, String phone) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}