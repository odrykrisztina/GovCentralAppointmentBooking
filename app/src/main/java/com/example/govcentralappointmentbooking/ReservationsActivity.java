package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Calendar tomorrow = Calendar.getInstance();

                    try {
                        String todayStr = sdf.format(new Date());
                        tomorrow.setTime(Objects.requireNonNull(sdf.parse(todayStr)));
                        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
                    } catch (ParseException e) {
                        Log.e("ReservationsActivity", "Dátumformázási hiba", e);
                        Toast.makeText(this, "Hibás dátumformátum.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String officeKey = doc.getString("officeKey");
                        String serviceKey = doc.getString("serviceKey");
                        String date = doc.getString("date");
                        String time = doc.getString("time");

                        String officeName = officeMap.containsKey(officeKey) ? officeMap.get(officeKey) : "Ismeretlen hivatal";
                        String serviceName = serviceMap.containsKey(serviceKey) ? serviceMap.get(serviceKey) : "Ismeretlen ügytípus";

                        String text = "Dátum: " + date +
                                "\nIdőpont: " + time +
                                "\nHelyszín: " + officeName +
                                "\nÜgytípus: " + serviceName;

                        TextView tv = new TextView(this);
                        tv.setText(text);
                        tv.setPadding(24, 24, 24, 24);
                        tv.setTextSize(14);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = GridLayout.LayoutParams.MATCH_PARENT;
                        params.setMargins(8, 8, 8, 8);
                        tv.setLayoutParams(params);

                        try {
                            Date bookingDate = sdf.parse(Objects.requireNonNull(date));
                            if (bookingDate != null && !bookingDate.before(tomorrow.getTime())) {
                                tv.setBackgroundResource(R.drawable.time_button_border_bold_blue);
                                tv.setOnClickListener(v -> confirmDelete(doc.getId()));
                            } else {
                                tv.setBackgroundResource(R.drawable.time_button_border);
                            }
                        } catch (ParseException e) {
                            tv.setBackgroundResource(R.drawable.time_button_border);
                        }

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

    private void confirmDelete(String documentId) {
        new AlertDialog.Builder(this)
                .setTitle("Foglalás törlése")
                .setIcon(R.drawable.question_mark_blue_24)
                .setMessage("Biztosan törlöd ezt a foglalást?")
                .setPositiveButton("Igen", (dialog, which) -> deleteReservation(documentId))
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void deleteReservation(String documentId) {
        FirebaseFirestore.getInstance()
                .collection("bookings")
                .document(documentId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Foglalás törölve.", Toast.LENGTH_SHORT).show();
                    loadUserReservations();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Hiba törlés közben: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
    }
}
