package com.example.govcentralappointmentbooking;

import com.example.govcentralappointmentbooking.utils.Util;
import com.example.govcentralappointmentbooking.utils.Validator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getName();

    private EditText userNameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameInput = findViewById(R.id.userNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        passwordConfirmInput = findViewById(R.id.passwordConfirmInput);

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            @SuppressLint("InflateParams") View popupView =
                    getLayoutInflater().inflate(R.layout.menu_popup_register, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.showAsDropDown(v, 0, 50);

            popupView.findViewById(R.id.menu_main).setOnClickListener(view -> {
                popupWindow.dismiss();
                Util.startActivityWithAnimation(
                        this, MainActivity.class);
            });

            popupView.findViewById(R.id.menu_login).setOnClickListener(view -> {
                popupWindow.dismiss();
                Util.startActivityWithAnimation(
                        this, LoginActivity.class);
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

    public void register(View view) {

        String userName = userNameInput.getText().toString().trim();
        if (!Validator.isValidUsername(userName)) {
            userNameInput.setError("Érvénytelen felhasználónév!");
            return;
        }

        String email = emailInput.getText().toString().trim();
        if (!Validator.isValidEmail(email)) {
            emailInput.setError("Érvénytelen e-mail cím!");
            return;
        }

        String phone = phoneInput.getText().toString().trim();
        if (!Validator.isValidPhoneNumber(phone)) {
            phoneInput.setError("Érvénytelen telefon!");
            return;
        }

        String password = passwordInput.getText().toString().trim();
        if (!Validator.isValidPassword(password)) {
            passwordInput.setError("Érvénytelen jelszó!\n" + Validator.PASSWORD_ROULE);
            return;
        }

        String passwordConfirm = passwordConfirmInput.getText().toString().trim();
        if (!password.equals(passwordConfirm)) {
            passwordConfirmInput.setError("Érvénytelen jelszó megerősítés!");
            return;
        }

        Log.i(LOG_TAG,
                "\nFelhasználó neve: " + userName +
                        "\nEmail cím: " + email +
                        "\nTelefon: " + phone +
                        "\nJelszó : " + password);

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("userName", userName);
                        userData.put("email", email);
                        userData.put("phone", phone);

                        db.collection("users")
                                .document(Objects.requireNonNull(user).getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Util.userUid = user.getUid();
                                    Util.userName = userName;
                                    Log.d(LOG_TAG,
                                            "Felhasználó Firestore-ban elmentve, azonosítója: "+
                                                    Util.userUid);

                                    new AlertDialog.Builder(this)
                                            .setTitle("Sikeres regisztráció")
                                            .setIcon(R.drawable.check_circle_green_24)
                                            .setMessage("Köszönjük, hogy regisztrált, " + userName + "!")
                                            .setPositiveButton("Tovább", (dialog, which) ->
                                                    Util.startActivityWithAnimation(
                                                            this, BookingActivity.class))
                                            .setCancelable(false)
                                            .show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this,
                                        "Adatmentés sikertelen: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show());

                    } else {
                        Toast.makeText(this,
                                "Regisztráció sikertelen: " +
                                        Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void loginView(View view) {
        Util.startActivityWithAnimation(
                this, LoginActivity.class);
    }
}