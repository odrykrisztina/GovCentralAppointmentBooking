package com.example.govcentralappointmentbooking.models;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class Service implements Serializable {

    public String key;
    public String name;

    public Service() {}

    public Service(String key, String name) {
        this.key = key;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
