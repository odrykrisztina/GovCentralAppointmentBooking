package com.example.govcentralappointmentbooking.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import com.example.govcentralappointmentbooking.R;

public class Util {

    public static String userUid = null;
    public static String timeSelected = null;

    public static void startActivityWithAnimation(
            Context context, Class<?> targetActivity) {

        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);

        if (context instanceof Activity) {
            if (Math.random() < 0.5)
                    ((Activity) context).overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left);
            else    ((Activity) context).overridePendingTransition(
                        R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
