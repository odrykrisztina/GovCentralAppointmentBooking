package com.example.govcentralappointmentbooking.utils;

import android.util.Patterns;

public class Validator {

    public static final String PASSWORD_ROULE =
            "(6-20 karakter, legalább egy kisbetűt, egy nagybetűt és egy számot tartalmaz)";
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) return false;
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,20}$";
        return password.matches(pattern);
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        phone = phone.replaceAll("[\\s\\-()]", "");
        String pattern = "^(\\+36|06)?[1-9][0-9]{7,8}$";
        return phone.matches(pattern);
    }

    public static boolean isValidUsername(String userName) {
        if (userName == null || userName.isEmpty()) return false;
        userName = userName.trim();
        if (userName.length() < 3 || userName.length() > 100) return false;
        return userName.matches("^[\\p{L} .'-]+$");
    }
}
