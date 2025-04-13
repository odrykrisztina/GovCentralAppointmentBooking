package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashSet;
import java.util.Set;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.app.AlertDialog;
import android.widget.Toast;

public class BookingFragment extends Fragment {

    private GridLayout timeTableGrid;
    private Button selectedButton = null;
    private Button saveButton;

    private final int[] minutes = {0, 15, 30, 45};
    private final int[]  hours = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17};

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        // Todo: Get blocked slots from booking time.

        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        Bundle args = getArguments();
        if (args != null) {

            String dateSelected = args.getString("dateSelected", "N/A");
            String officeSelectedKey = args.getString("officeSelectedKey");
            String officeSelectedName = args.getString("officeSelectedName", "N/A");
            String serviceSelectedKey = args.getString("serviceSelectedKey");
            String serviceSelectedName = args.getString("serviceSelectedName", "N/A");

            TextView dateText = view.findViewById(R.id.dateText);
            TextView officeText = view.findViewById(R.id.officeText);
            TextView serviceText = view.findViewById(R.id.serviceText);

            dateText.setText(dateSelected);
            officeText.setText(officeSelectedName);
            serviceText.setText(serviceSelectedName);

            loadBookingsFromFirestore(dateSelected, officeSelectedKey, serviceSelectedKey);
        }

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setEnabled(false);
        saveButton.setAlpha(0.6f);
        Util.timeSelected = null;

        timeTableGrid = view.findViewById(R.id.timeTableGrid);
        //generateTimeTable();

        return view;
    }

    private void loadBookingsFromFirestore(String date, String officeKey, String serviceKey) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bookings")
                .whereEqualTo("date", date)
                .whereEqualTo("officeKey", officeKey)
                .whereEqualTo("serviceKey", serviceKey)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Set<String> blockedSlots = new HashSet<>();
                    Set<String> mySlots = new HashSet<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String time = doc.getString("time");
                        String userUid = doc.getString("userUid");

                        if (time != null) {
                            blockedSlots.add(time);
                            if (userUid != null && userUid.equals(Util.userUid)) {
                                mySlots.add(time);
                            }
                        }
                    }

                    // Most már betölthetjük az időtáblázatot
                    generateTimeTable(blockedSlots, mySlots);
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Foglalások lekérdezése sikertelen: ", e));
    }

    private void generateTimeTable(Set<String> blockedSlots, Set<String> mySlots) {

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

                if (mySlots.contains(time)) {
                    btn.setBackgroundColor(Color.parseColor("#2196F3")); // kék
                    btn.setTextColor(Color.WHITE);
                    btn.setOnClickListener(v -> handleOwnBooking((Button) v)); // saját foglalás esetén törléshez
                } else if (blockedSlots.contains(time)) {
                    btn.setBackgroundColor(Color.parseColor("#FF0000")); // piros
                    btn.setTextColor(Color.WHITE);
                    btn.setEnabled(false);
                } else {
                    btn.setBackgroundColor(Color.WHITE);
                    btn.setBackgroundResource(R.drawable.time_button_border);
                    btn.setOnClickListener(v -> handleSelection((Button) v)); // új foglalás
                }

                timeTableGrid.addView(btn);
            }
        }
    }

    private void handleOwnBooking(Button btn) {
        String time = (String) btn.getTag();

        new AlertDialog.Builder(getContext())
                .setTitle("Foglalás törlése")
                .setIcon(R.drawable.question_mark_blue_24)
                .setMessage("Biztosan törlöd ezt az időpontot?\n" + time)
                .setPositiveButton("Igen", (dialog, which) -> deleteBooking(time))
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void deleteBooking(String time) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        assert getArguments() != null;
        db.collection("bookings")
                .whereEqualTo("userUid", Util.userUid)
                .whereEqualTo("date", getArguments().getString("dateSelected"))
                .whereEqualTo("officeKey", getArguments().getString("officeSelectedKey"))
                .whereEqualTo("serviceKey", getArguments().getString("serviceSelectedKey"))
                .whereEqualTo("time", time)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        doc.getReference().delete(); // foglalás törlése
                    }

                    Toast.makeText(getContext(),
                            "Foglalás törölve: " + time,
                            Toast.LENGTH_SHORT).show();

                    // Frissítsd újra a foglalásokat
                    loadBookingsFromFirestore(
                            getArguments().getString("dateSelected"),
                            getArguments().getString("officeSelectedKey"),
                            getArguments().getString("serviceSelectedKey")
                    );
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Hiba a törléskor: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
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