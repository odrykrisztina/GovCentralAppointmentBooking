package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.govcentralappointmentbooking.models.Service;
import com.example.govcentralappointmentbooking.utils.OfficeUtils;
import com.example.govcentralappointmentbooking.utils.Util;
import com.example.govcentralappointmentbooking.utils.DataProvider;
import com.example.govcentralappointmentbooking.models.Office;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();

    private static final int REQUEST_CALL_PERMISSION = 1;

    private Office selectedOffice;
    private Service selectedService;
    private EditText dateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Office select
        List<Office> officeList = DataProvider.getOfficeList();
        ArrayAdapter<Office> officeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, officeList);
        Spinner officeSpinner = findViewById(R.id.officeSpinner);
        officeSpinner.setAdapter(officeAdapter);
        officeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOffice = officeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // Service select
        List<Service> serviceList = DataProvider.getServiceList();
        ArrayAdapter<Service> serviceAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, serviceList);
        Spinner serviceSpinner = findViewById(R.id.serviceSpinner);
        serviceSpinner.setAdapter(serviceAdapter);
        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedService = serviceList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
        if (Util.userUid == null) {
            Toast.makeText(this, "Kérem, jelentkezzen be!", Toast.LENGTH_LONG).show();
            Util.finishWithAnimation(this);
        }
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
        intent.putExtra("selectedOffice", selectedOffice);
        intent.putExtra("selectedService", selectedService);
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

    public void reservations(View view) {
        Util.startActivityWithAnimation(
                this, ReservationsActivity.class);
    }

    public void openOfficeMap(View view) {
        OfficeUtils.openMap(this, selectedOffice);
    }

    public void callOffice(View view) {
        OfficeUtils.call(this, selectedOffice, REQUEST_CALL_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Telefonhívás")
                        .setMessage("Biztosan felhívod a kiválasztott hivatalt?")
                        .setPositiveButton("Igen", (dialog, which) ->
                                OfficeUtils.startCall(this, selectedOffice))
                        .setNegativeButton("Mégse", null)
                        .show();
            } else {
                Toast.makeText(this, "Hívási engedély megtagadva!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}