package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.govcentralappointmentbooking.utils.Util;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();

    private String officeSelectedName;
    private String officeSelectedKey;
    private String serviceSelectedName;
    private String serviceSelectedKey;
    private EditText dateInput;

    @NonNull
    static LinkedHashMap<String, String> getStringOfficeHashMap() {
        LinkedHashMap<String, String> officeMap = new LinkedHashMap<>();
        officeMap.put("RAKO", "6722 Szeged, Rákóczi tér 1.");
        officeMap.put("KERE", "6728 Szeged, Kereskedő köz 5/A-B.");
        officeMap.put("SZOR", "6726 Szeged, Szőregi út 80.");
        return officeMap;
    }

    @NonNull
    static LinkedHashMap<String, String> getStringServiceHashMap() {
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

        // Office select
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


        // Service select
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

        // Date input
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

        // Menu
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            @SuppressLint("InflateParams") View popupView =
                    getLayoutInflater().inflate(R.layout.menu_popup_booking, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.showAsDropDown(v, 0, 50);

            TextView userNameText = popupView.findViewById(R.id.userName);
            userNameText.setText(Util.userName);

            popupView.findViewById(R.id.menu_profile).setOnClickListener(view -> {
                popupWindow.dismiss();
                Util.startActivityWithAnimation(
                        this, ProfileActivity.class);
            });

            popupView.findViewById(R.id.menu_reservations).setOnClickListener(view -> {
                popupWindow.dismiss();
                Util.startActivityWithAnimation(
                        this, ReservationsActivity.class);
            });

            popupView.findViewById(R.id.menu_logout).setOnClickListener(view -> {
                popupWindow.dismiss();
                this.confirmExit();
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

    public void timeSelect(View view) {

        String dateSelected = dateInput.getText().toString().trim();
        if (dateSelected.isEmpty()) {
            Toast toast = Toast.makeText(BookingActivity.this,
                    "Időpontot kell vállasztani!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        Intent intent = new Intent(this, BookingTimeActivity.class);
        intent.putExtra("officeSelectedName", officeSelectedName);
        intent.putExtra("officeSelectedKey", officeSelectedKey);
        intent.putExtra("serviceSelectedName", serviceSelectedName);
        intent.putExtra("serviceSelectedKey", serviceSelectedKey);
        intent.putExtra("dateSelected", dateSelected);
        startActivity(intent);
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle("Kilépés")
                .setMessage("Biztosan ki szeretnél lépni?")
                .setIcon(R.drawable.question_mark_blue_24)
                .setPositiveButton("Igen", (dialog, which) -> {
                    Util.userUid = null;
                    Util.userName = null;
                    Util.startActivityWithAnimation(this, MainActivity.class);
                })
                .setNegativeButton("Mégse", null)
                .show();
    }
}