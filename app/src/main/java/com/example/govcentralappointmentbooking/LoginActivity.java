package com.example.govcentralappointmentbooking;

import com.example.govcentralappointmentbooking.utils.Util;
import com.example.govcentralappointmentbooking.utils.Validator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Util.userUid = user != null ? user.getUid() : null;

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document(Util.userUid)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    if (documentSnapshot.exists()) {
                                        String userName = documentSnapshot.getString("userName");
                                        Util.userName = userName;

                                        new AlertDialog.Builder(this)
                                                .setTitle("Sikeres bejelentkezés")
                                                .setIcon(R.drawable.check_circle_green_24)
                                                .setMessage("Üdvözöllek, " + userName + "!")
                                                .setPositiveButton("OK", (dialog, which) -> {
                                                    Util.startActivityWithAnimation(this, BookingActivity.class);
                                                })
                                                .setCancelable(false)
                                                .show();
                                    } else {
                                        Toast.makeText(this,
                                                "Felhasználói adatok nem találhatók!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this,
                                            "Hiba a Firestore lekérdezéskor: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {
                        Toast.makeText(this,
                                "Hibás e-mail vagy jelszó!",
                                Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void mainView(View view) {
        Util.userUid = null;
        Util.userName = null;
        Util.startActivityWithAnimation(
                this, MainActivity.class);
    }

    public void registerView(View view) {
        Util.startActivityWithAnimation(
                this, RegisterActivity.class);
    }
}