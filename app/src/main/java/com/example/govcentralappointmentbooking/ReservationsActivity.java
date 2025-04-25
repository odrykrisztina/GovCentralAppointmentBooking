package com.example.govcentralappointmentbooking;

import static android.view.View.VISIBLE;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.govcentralappointmentbooking.models.Booking;
import com.example.govcentralappointmentbooking.utils.Util;
import com.example.govcentralappointmentbooking.utils.DataProvider;
import com.example.govcentralappointmentbooking.models.Office;
import com.example.govcentralappointmentbooking.models.Service;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
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
                Util.finishWithAnimation(this);
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
        loadUserReservations();
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
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING)
                .orderBy("officeKey", Query.Direction.ASCENDING)
                .orderBy("serviceKey", Query.Direction.ASCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(querySnapshots -> {

                    reservationsTableGrid.removeAllViews();
                    userNameText.setText(Util.userName);

                    Map<String, String> officeMap = new HashMap<>();
                    for (Office office : DataProvider.getOfficeList()) {
                        officeMap.put(office.key, office.name);
                    }

                    Map<String, String> serviceMap = new HashMap<>();
                    for (Service service : DataProvider.getServiceList()) {
                        serviceMap.put(service.key, service.name);
                    }

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

                    boolean canBeDeleted = false;

                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        Booking booking = doc.toObject(Booking.class);

                        String officeName = officeMap.containsKey(booking.officeKey) ?
                                officeMap.get(booking.officeKey) : "Ismeretlen hivatal";

                        String serviceName = serviceMap.containsKey(booking.serviceKey) ?
                                serviceMap.get(booking.serviceKey) : "Ismeretlen ügytípus";

                        String text = "Dátum: " + booking.date +
                                "\nIdőpont: " + booking.time +
                                "\nHelyszín: " + officeName +
                                "\nÜgytípus: " + serviceName;

                        TextView tv = new TextView(this);
                        tv.setText(text);
                        tv.setPadding(24, 24, 24, 24);
                        tv.setTextSize(12);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = GridLayout.LayoutParams.MATCH_PARENT;
                        params.setMargins(8, 8, 8, 8);
                        tv.setLayoutParams(params);

                        try {
                            Date bookingDate = sdf.parse(Objects.requireNonNull(booking.date));
                            if (bookingDate != null && !bookingDate.before(tomorrow.getTime())) {
                                tv.setBackgroundResource(R.drawable.time_button_border_bold_blue);
                                tv.setOnClickListener(v -> confirmDelete(doc.getId()));
                                canBeDeleted = true;
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
                        tv.setTextSize(18);
                        tv.setGravity(Gravity.CENTER);
                        tv.setTypeface(null, Typeface.BOLD);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                        params.width = GridLayout.LayoutParams.MATCH_PARENT;
                        params.setMargins(0, 50, 0, 50);
                        tv.setLayoutParams(params);

                        reservationsTableGrid.addView(tv);
                    }

                    if (canBeDeleted) {
                        LinearLayout info = findViewById(R.id.information);
                        info.setVisibility(VISIBLE);
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

    public void goBack(View view) {
        Util.finishWithAnimation(this);
    }
}
