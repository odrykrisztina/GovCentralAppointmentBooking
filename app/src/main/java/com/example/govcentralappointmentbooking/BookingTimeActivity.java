package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.govcentralappointmentbooking.utils.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashSet;
import java.util.Set;
import com.example.govcentralappointmentbooking.models.Booking;

public class BookingTimeActivity extends AppCompatActivity {

    private static final String LOG_TAG = BookingTimeActivity.class.getName();

    private String officeSelectedName;
    private String officeSelectedKey;
    private String serviceSelectedName;
    private String serviceSelectedKey;
    private String dateSelected;

    private GridLayout timeTableGrid;
    private Button selectedButton = null;
    private Button saveButton;

    private final int[] minutes = {0, 15, 30, 45};
    private final int[] hours = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_time);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setEnabled(false);
        saveButton.setAlpha(0.6f);
        Util.timeSelected = null;

        officeSelectedName = getIntent().getStringExtra("officeSelectedName");
        officeSelectedKey = getIntent().getStringExtra("officeSelectedKey");
        serviceSelectedName = getIntent().getStringExtra("serviceSelectedName");
        serviceSelectedKey = getIntent().getStringExtra("serviceSelectedKey");
        dateSelected = getIntent().getStringExtra("dateSelected");

        TextView dateText = findViewById(R.id.dateText);
        TextView officeText = findViewById(R.id.officeText);
        TextView serviceText = findViewById(R.id.serviceText);
        timeTableGrid = findViewById(R.id.timeTableGrid);

        dateText.setText(dateSelected);
        officeText.setText(officeSelectedName);
        serviceText.setText(serviceSelectedName);

        loadBookingsFromFirestore();

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

            TextView userNameText = popupView.findViewById(R.id.userName);
            userNameText.setText(Util.userName);

            popupView.findViewById(R.id.menu_back).setOnClickListener(view -> {
                popupWindow.dismiss();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
                finish();
            });
        });

        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        if (Util.timeSelected != null) {
            Util.timeSelected = null;
            saveButton.setEnabled(false);
            saveButton.setAlpha(0.6f);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    private void loadBookingsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bookings")
                .whereEqualTo("date", dateSelected)
                .whereEqualTo("officeKey", officeSelectedKey)
                .whereEqualTo("serviceKey", serviceSelectedKey)
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

                    generateTimeTable(blockedSlots, mySlots);
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Foglalások lekérdezése sikertelen: ", e));
    }

    private void generateTimeTable(Set<String> blockedSlots, Set<String> mySlots) {
        Context ctx = this;
        timeTableGrid.removeAllViews();

        addLabelCell(ctx, "");
        for (int min : minutes) {
            addLabelCell(ctx, min + " perc");
        }

        for (int hour : hours) {
            addLabelCell(ctx, hour + ":00");
            for (int min : minutes) {
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d", hour, min);

                Button btn = new Button(ctx);
                btn.setText(time);
                btn.setTag(time);
                btn.setTextSize(12);
                btn.setTextColor(Color.BLACK);
                btn.setPadding(6, 6, 6, 6);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = (int) (getResources().getDisplayMetrics().density * 36);
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(4, 4, 4, 4);
                btn.setLayoutParams(params);

                if (mySlots.contains(time)) {
                    btn.setBackgroundColor(Color.parseColor("#2196F3"));
                    btn.setTextColor(Color.WHITE);
                    btn.setOnClickListener(v -> handleOwnBooking((Button) v));
                } else if (blockedSlots.contains(time)) {
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

    private void handleOwnBooking(Button btn) {
        String time = (String) btn.getTag();

        new AlertDialog.Builder(this)
                .setTitle("Foglalás törlése")
                .setIcon(R.drawable.question_mark_blue_24)
                .setMessage("Biztosan törlöd ezt az időpontot?\n" + time)
                .setPositiveButton("Igen", (dialog, which) -> deleteBooking(time))
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void deleteBooking(String time) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bookings")
                .whereEqualTo("userUid", Util.userUid)
                .whereEqualTo("date", dateSelected)
                .whereEqualTo("officeKey", officeSelectedKey)
                .whereEqualTo("serviceKey", serviceSelectedKey)
                .whereEqualTo("time", time)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        doc.getReference().delete();
                    }
                    Toast.makeText(this, "Foglalás törölve: " + time, Toast.LENGTH_SHORT).show();
                    loadBookingsFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Hiba a törléskor: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
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

    public void confirmSave(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Időpont foglalás")
                .setIcon(R.drawable.question_mark_blue_24)
                .setMessage("Biztosan lefoglalod az időpontot?" +
                        "\nKormányablak:\n" + officeSelectedName +
                        "\nSzolgáltatás:\n" + serviceSelectedName +
                        "\nDátum: " + dateSelected +
                        "\nIdőpont: " + Util.timeSelected)
                .setPositiveButton("Igen", (dialog, which) -> bookingSave())
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void bookingSave() {

        Booking booking = new Booking(
                Util.userUid,
                officeSelectedKey,
                serviceSelectedKey,
                dateSelected,
                Util.timeSelected,
                Timestamp.now()
        );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this,
                            "Foglalás sikeres!",
                            Toast.LENGTH_LONG).show();
                    overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Hiba a foglalás mentésekor: ", e);
                    Toast.makeText(this,
                            "Foglalás sikertelen: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    public void openOfficeMap(View view) {
        if (officeSelectedName == null || officeSelectedName.isEmpty()) {
            Toast.makeText(this,
                    "Előbb válassz egy hivatalt!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" +
                    Uri.encode(officeSelectedName));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Nem sikerült megnyitni a térképet: " +
                    e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
