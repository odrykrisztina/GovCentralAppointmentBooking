package com.example.govcentralappointmentbooking.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.govcentralappointmentbooking.models.Office;

public class OfficeUtils {

    public static void openMap(Context context, Office office) {
        if (office == null || office.name == null || office.name.isEmpty()) {
            Toast.makeText(context, "Előbb válassz egy hivatalt!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" +
                    Uri.encode(office.name));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Nem sikerült megnyitni a térképet: " +
                    e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void call(Context context, Office office, int requestCode) {
        if (office == null || office.phone == null) {
            Toast.makeText(context, "Nincs telefonszám!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CALL_PHONE}, requestCode);
        } else {
            startCall(context, office);
        }
    }

    public static void startCall(Context context, Office office) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + office.phone));
        context.startActivity(intent);
    }
}

