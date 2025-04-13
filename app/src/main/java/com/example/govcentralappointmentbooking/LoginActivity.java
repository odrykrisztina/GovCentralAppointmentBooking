package com.example.govcentralappointmentbooking;

import com.example.govcentralappointmentbooking.utils.Util;
import com.example.govcentralappointmentbooking.utils.Validator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();

    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

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

    public void login(View view) {

        String email = emailInput.getText().toString().trim();
        if (!Validator.isValidEmail(email)) {
            emailInput.setError("Érvénytelen e-mail cím!");
            return;
        }

        String password = passwordInput.getText().toString().trim();
        if (!Validator.isValidPassword(password)) {
            passwordInput.setError("Érvénytelen jelszó!\n" + Validator.PASSWORD_ROULE);
            return;
        }

        Log.i(LOG_TAG,"\nEmail cím: " + email +
                            "\nJelszó: " + password);

        // Todo: Check user exist.
        //       When exist get user identifier.
        //       Otherwise shoe error.

        Util.userId = 66;
        Util.startActivityWithAnimation(this, BookingActivity.class);
    }

    public void mainView(View view) {
        Util.userId = 0;
        Util.startActivityWithAnimation(
                this, MainActivity.class);
    }

    public void registerView(View view) {
        Util.startActivityWithAnimation(this, RegisterActivity.class);
    }
}