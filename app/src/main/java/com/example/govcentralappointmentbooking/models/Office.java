package com.example.govcentralappointmentbooking.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Office implements Serializable {
    public String key;
    public String name;
    public String phone;

    public Office() {}

    public Office(String key, String name, String phone) {
        this.key = key;
        this.name = name;
        this.phone = phone;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}

