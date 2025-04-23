package com.example.govcentralappointmentbooking.models;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class Booking {

    public String userUid;
    public String officeKey;
    public String serviceKey;
    public String date;
    public String time;
    public Timestamp createdAt;

    public Booking() {}

    public Booking(String userUid, String officeKey, String serviceKey,
                   String date, String time, Timestamp createdAt) {
        this.userUid = userUid;
        this.officeKey = officeKey;
        this.serviceKey = serviceKey;
        this.date = date;
        this.time = time;
        this.createdAt = createdAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "Booking{" +
                "userUid='" + userUid + '\'' +
                ", officeKey='" + officeKey + '\'' +
                ", serviceKey='" + serviceKey + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
