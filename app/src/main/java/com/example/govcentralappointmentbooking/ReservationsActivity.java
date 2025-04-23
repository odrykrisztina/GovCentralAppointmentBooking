package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.govcentralappointmentbooking.utils.Util;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Map;

public class ReservationsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private GridLayout reservationsTableGrid;

    private TextView userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        reservationsTableGrid = findViewById(R.id.reservationsTableGrid);
        userNameText = findViewById(R.id.userName);

        loadUserReservations();

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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    private void loadUserReservations() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bookings")
                .whereEqualTo("userUid", Util.userUid)
                .get()
                .addOnSuccessListener(querySnapshots -> {

                    reservationsTableGrid.removeAllViews();
                    userNameText.setText(Util.userName);

                    Map<String, String> officeMap = BookingActivity.getStringOfficeHashMap();
                    Map<String, String> serviceMap = BookingActivity.getStringServiceHashMap();

                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String officeKey = doc.getString("officeKey");
                        String serviceKey = doc.getString("serviceKey");
                        String date = doc.getString("date");
                        String time = doc.getString("time");

                        String officeName = officeMap.containsKey(officeKey)
                                ? officeMap.get(officeKey)
                                : "Ismeretlen hivatal";

                        String serviceName = serviceMap.containsKey(serviceKey)
                                ? serviceMap.get(serviceKey)
                                : "Ismeretlen ügytípus";

                        String text = "Dátum: " + date +
                                "\nIdőpont: " + time +
                                "\nHelyszín: " + officeName +
                                "\nÜgytípus: " + serviceName;

                        TextView tv = new TextView(this);
                        tv.setText(text);
                        tv.setPadding(24, 24, 24, 24);
                        tv.setTextSize(14);
                        tv.setBackgroundResource(R.drawable.time_button_border);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = GridLayout.LayoutParams.MATCH_PARENT;
                        params.setMargins(8, 8, 8, 8);
                        tv.setLayoutParams(params);

                        reservationsTableGrid.addView(tv);
                    }

                    if (querySnapshots.isEmpty()) {
                        TextView tv = new TextView(this);
                        tv.setText(R.string.no_reservation);
                        tv.setTextSize(16);
                        tv.setPadding(20, 20, 20, 20);
                        reservationsTableGrid.addView(tv);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Hiba történt: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}