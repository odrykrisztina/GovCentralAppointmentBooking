package com.example.govcentralappointmentbooking.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import com.example.govcentralappointmentbooking.R;

public class Util {

    public static String userUid = null;
    public static String userName = null;
    public static String timeSelected = null;

    // etc
    private static final int[] inAnimations = {
            R.anim.slide_in_left,
            R.anim.slide_in_right,
            R.anim.fade_in,
            R.anim.scale_in
    };

    private static final int[] outAnimations = {
            R.anim.slide_out_left,
            R.anim.slide_out_right,
            R.anim.fade_out,
            R.anim.scale_out
    };

    public static void startActivityWithAnimation(
            Context context, Class<?> targetActivity) {

        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);

        if (context instanceof Activity) {

            int inAnimationResource;
            int outAnimationResource;

            if (targetActivity.getSimpleName().equals("BookingTimeActivity")) {
                inAnimationResource = R.anim.scale_in_slow;
                outAnimationResource = R.anim.scale_out_slow;
            } else {
                inAnimationResource = getRandom(inAnimations);
                outAnimationResource = getRandom(outAnimations);
            }
            ((Activity) context).overridePendingTransition(
                    inAnimationResource, outAnimationResource);
        }
    }

    public static void finishWithAnimation(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(
                getRandom(inAnimations), getRandom(outAnimations));
    }

    private static int getRandom(int[] array) {
        return array[(int) (Math.random() * array.length)];
    }
}
