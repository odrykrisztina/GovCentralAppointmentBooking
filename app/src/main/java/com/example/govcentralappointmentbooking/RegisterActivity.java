package com.example.govcentralappointmentbooking;

import com.example.govcentralappointmentbooking.utils.Util;
import com.example.govcentralappointmentbooking.utils.Validator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

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

        // Todo: Check user exist
        Util.userId = 66;

        Log.i(LOG_TAG, "Regisztrált: " + userName + ", email: " + email +
                ", telefon: " + phone + ", jelszó: " + password);

        Util.startActivityWithAnimation(this, BookingActivity.class);
    }

    public void mainView(View view) {
        Util.userId = 0;
        Util.startActivityWithAnimation(
                this, MainActivity.class);
    }

    public void loginView(View view) {
        Util.startActivityWithAnimation(
                this, LoginActivity.class);
    }
}