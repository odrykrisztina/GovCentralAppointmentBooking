package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.govcentralappointmentbooking.utils.Util;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BookingFragment extends Fragment {

    private GridLayout timeTableGrid;
    private Button selectedButton = null;

    private Button saveButton;

    private final int[] minutes = {0, 15, 30, 45};
    private final int[]  hours = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17};

    Set<String> blockedSlots = new HashSet<>(
            Arrays.asList("09:15", "10:00", "10:15", "10:30", "10:45", "11:30", "13:00",
                            "12:15", "13:45", "16:10", "16:30", "16:45", "17:00"));

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        Bundle args = getArguments();
        if (args != null) {

            String dateSelected = args.getString("dateSelected", "N/A");
            String officeSelectedName = args.getString("officeSelectedName", "N/A");
            String serviceSelectedName = args.getString("serviceSelectedName", "N/A");

            TextView dateText = view.findViewById(R.id.dateText);
            TextView officeText = view.findViewById(R.id.officeText);
            TextView serviceText = view.findViewById(R.id.serviceText);

            dateText.setText(dateSelected);
            officeText.setText(officeSelectedName);
            serviceText.setText(serviceSelectedName);
        }

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setEnabled(false);
        saveButton.setAlpha(0.6f);
        Util.timeSelected = null;

        timeTableGrid = view.findViewById(R.id.timeTableGrid);
        generateTimeTable();

        return view;
    }

    private void generateTimeTable() {

        Context ctx = getContext();
        timeTableGrid.removeAllViews();

        addLabelCell(ctx, "");
        for (int min : minutes) {
            addLabelCell(ctx, min + " perc");
        }

        for (int hour : hours) {

            addLabelCell(ctx, hour + ":00");
            for (int min : minutes) {
                @SuppressLint("DefaultLocale") String time =
                        String.format("%02d:%02d", hour, min);

                Button btn = new Button(ctx);
                btn.setText(time);
                btn.setTag(time);
                btn.setTextSize(12);
                btn.setTextColor(Color.BLACK);
                btn.setPadding(6, 6, 6, 6);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(4, 4, 4, 4);
                params.height = (int) (getResources().getDisplayMetrics().density * 36);
                btn.setLayoutParams(params);

                if (blockedSlots.contains(time)) {
                    btn.setBackgroundColor(Color.parseColor("#FF0000"));
                    btn.setTextColor(Color.WHITE);
                    btn.setEnabled(false);
                } else {
                    btn.setBackgroundColor(Color.WHITE);
                    btn.setBackgroundResource(R.drawable.time_button_border);
                    btn.setOnClickListener(v -> handleSelection((Button) v));
                }

                timeTableGrid.addView(btn);
            }
        }
    }

    private void addLabelCell(Context ctx, String text) {

        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        tv.setLayoutParams(params);

        timeTableGrid.addView(tv);
    }

    private void handleSelectionA(Button btn) {

        if (selectedButton == btn) {
            btn.setBackgroundColor(Color.WHITE);
            btn.setTextColor(Color.BLACK);
            btn.setBackgroundResource(R.drawable.time_button_border);

            saveButton.setEnabled(false);
            saveButton.setAlpha(0.6f);
            Util.timeSelected = null;
            return;
        } else {
            if (selectedButton != null) {
                selectedButton.setBackgroundColor(Color.WHITE);
                selectedButton.setTextColor(Color.BLACK);
                selectedButton.setBackgroundResource(R.drawable.time_button_border);
            }
            saveButton.setEnabled(true);
            saveButton.setAlpha(1.0f);
            Util.timeSelected = selectedButton.getTag().toString();

            btn.setBackgroundColor(Color.parseColor("#008000"));
            btn.setTextColor(Color.WHITE);
            selectedButton = btn;
        }
    }

    private void handleSelection(Button btn) {

        if (selectedButton == btn) {
            selectedButton.setBackgroundColor(Color.WHITE);
            selectedButton.setTextColor(Color.BLACK);
            selectedButton.setBackgroundResource(R.drawable.time_button_border);
            selectedButton = null;

            saveButton.setEnabled(false);
            saveButton.setAlpha(0.6f);
            Util.timeSelected = null;
            return;
        }

        if (selectedButton != null) {
            selectedButton.setBackgroundColor(Color.WHITE);
            selectedButton.setTextColor(Color.BLACK);
            selectedButton.setBackgroundResource(R.drawable.time_button_border);
        }

        selectedButton = btn;
        selectedButton.setBackgroundColor(Color.parseColor("#008000"));
        selectedButton.setTextColor(Color.WHITE);

        saveButton.setEnabled(true);
        saveButton.setAlpha(1.0f);
        Util.timeSelected = selectedButton.getTag().toString();
    }
}