package com.example.govcentralappointmentbooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.govcentralappointmentbooking.utils.Util;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Map;

public class ReservationsFragment extends Fragment {

    private GridLayout reservationsTableGrid;

    private TextView userNameText;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_reservations,
                container, false);

        reservationsTableGrid = view.findViewById(R.id.reservationsTableGrid);
        userNameText = view.findViewById(R.id.userName);

        loadUserReservations();
        return view;
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

                        TextView tv = new TextView(getContext());
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
                        TextView tv = new TextView(getContext());
                        tv.setText("Nincs korábbi foglalás.");
                        tv.setTextSize(16);
                        tv.setPadding(20, 20, 20, 20);
                        reservationsTableGrid.addView(tv);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Hiba történt: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}