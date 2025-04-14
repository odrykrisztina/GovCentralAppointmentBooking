package com.example.govcentralappointmentbooking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.govcentralappointmentbooking.utils.Util;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

public class BookingActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();

    private String officeSelectedName;
    private String officeSelectedKey;
    private String serviceSelectedName;
    private String serviceSelectedKey;
    private String dateSelected;

    private EditText dateInput;

    @NonNull
    private static LinkedHashMap<String, String> getStringOfficeHashMap() {
        LinkedHashMap<String, String> officeMap = new LinkedHashMap<>();
        officeMap.put("RAKO", "6722 Szeged, Rákóczi tér 1.");
        officeMap.put("KERE", "6728 Szeged, Kereskedő köz 5/A-B.");
        officeMap.put("SZOR", "6726 Szeged, Szőregi út 80.");
        return officeMap;
    }

    @NonNull
    private static LinkedHashMap<String, String> getStringServiceHashMap() {
        LinkedHashMap<String, String> serviceMap = new LinkedHashMap<>();
        serviceMap.put("DIGA", "Állampolgárság");
        serviceMap.put("GEPJ", "Gépjármű");
        serviceMap.put("SZIG", "Igazolvány");
        serviceMap.put("UTLE", "Úlevél");
        serviceMap.put("UREG", "Ügyfélkapu");
        serviceMap.put("VEZE", "Vezetői engedély");
        serviceMap.put("EGYE", "Egyéb");
        return serviceMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        LinkedHashMap<String, String> officeMap = getStringOfficeHashMap();
        Spinner officeSpinner = findViewById(R.id.officeSpinner);
        officeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                officeSelectedName = parent.getItemAtPosition(position).toString();
                for (Map.Entry<String, String> entry : officeMap.entrySet()) {
                    if (entry.getValue().equals(officeSelectedName)) {
                        officeSelectedKey = entry.getKey();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayList<String> officeNames = new ArrayList<>(officeMap.values());
        ArrayAdapter<String> adapterOffice = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, officeNames);
        officeSpinner.setAdapter(adapterOffice);


        LinkedHashMap<String, String> serviceMap = getStringServiceHashMap();
        Spinner serviceSpinner = findViewById(R.id.serviceSpinner);
        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serviceSelectedName = parent.getItemAtPosition(position).toString();
                for (Map.Entry<String, String> entry : serviceMap.entrySet()) {
                    if (entry.getValue().equals(serviceSelectedName)) {
                        serviceSelectedKey = entry.getKey();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayList<String> serviceNames = new ArrayList<>(serviceMap.values());
        ArrayAdapter<String> adapterService = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, serviceNames);
        serviceSpinner.setAdapter(adapterService);


        dateInput = findViewById(R.id.dateInput);
        dateInput.setOnClickListener(v -> {

            Calendar today = Calendar.getInstance();
            today.add(Calendar.DAY_OF_YEAR, 1);
            Calendar maxDate = (Calendar) today.clone();
            maxDate.add(Calendar.DAY_OF_YEAR, 20);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    BookingActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {

                        Calendar dateSelected = Calendar.getInstance();
                        dateSelected.set(selectedYear, selectedMonth, selectedDay);

                        int dayOfWeek = dateSelected.get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                            Toast toast = Toast.makeText(BookingActivity.this,
                                    "Hétvégén nem lehet időpontot választani!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            dateInput.setText("");
                            return;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        dateInput.setText(sdf.format(dateSelected.getTime()));
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            datePickerDialog.show();
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainer);
            if (fragment == null) {

                View formBlock = findViewById(R.id.formFirstBlock);
                View fragmentContainer = findViewById(R.id.fragmentContainer);

                Animation inAnim = AnimationUtils
                        .loadAnimation(this, R.anim.slide_in_left);
                formBlock.startAnimation(inAnim);
                formBlock.setVisibility(View.VISIBLE);

                Animation outAnim = AnimationUtils
                        .loadAnimation(this, R.anim.slide_out_right);
                fragmentContainer.startAnimation(outAnim);
                fragmentContainer.setVisibility(View.GONE);
            }
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

    public void nextStep(View view) {

        dateSelected = dateInput.getText().toString().trim();
        if (dateSelected.isEmpty()) {
            Toast toast = Toast.makeText(BookingActivity.this,
                    "Időpontot kell vállasztani!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        findViewById(R.id.formFirstBlock).setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

        Bundle args = new Bundle();
        args.putString("dateSelected", dateSelected);
        args.putString("officeSelectedName", officeSelectedName);
        args.putString("officeSelectedKey", officeSelectedKey);
        args.putString("serviceSelectedName", serviceSelectedName);
        args.putString("serviceSelectedKey", serviceSelectedKey);

        BookingFragment fragment = new BookingFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();

        Log.i(LOG_TAG, getString(R.string.appointment) + " " + getString(R.string.to_book) );
    }

    public void goBack(View view) {
        getSupportFragmentManager()
                .popBackStack();
    }

    public void mainView(View view) {
        confirmExit();
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle("Kilépés")
                .setMessage("Biztosan ki szeretnél lépni?")
                .setIcon(R.drawable.question_mark_blue_24)
                .setPositiveButton("Igen", (dialog, which) -> {
                    Util.userUid = null;
                    Util.startActivityWithAnimation(this, MainActivity.class);
                })
                .setNegativeButton("Mégse", null)
                .show();
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> booking = new HashMap<>();
        booking.put("userUid", Util.userUid);
        booking.put("officeKey", officeSelectedKey);
        booking.put("serviceKey", serviceSelectedKey);
        booking.put("date", dateSelected);
        booking.put("time", Util.timeSelected);
        booking.put("createdAt", Timestamp.now());

        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Log.i(LOG_TAG, "Foglalás elmentve Firestore-ba: " + documentReference.getId());

                    // Visszalépünk, újra betöltheti a foglalásokat, stb.
                    getSupportFragmentManager().popBackStack();
                    Toast.makeText(this,
                            "Foglalás sikeres!",
                            Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Hiba a foglalás mentésekor: ", e);
                    Toast.makeText(this,
                            "Foglalás sikertelen: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}