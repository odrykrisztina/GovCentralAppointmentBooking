package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;

public class BookingTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_time);

        // Menu
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            @SuppressLint("InflateParams") View popupView =
                    getLayoutInflater().inflate(R.layout.menu_popup_back, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.showAsDropDown(v, 0, 50);

            popupView.findViewById(R.id.menu_back).setOnClickListener(view -> {
                popupWindow.dismiss();
                finish();
            });
        });
    }
}