package com.example.govcentralappointmentbooking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.govcentralappointmentbooking.models.User;
import com.example.govcentralappointmentbooking.utils.Util;
import com.example.govcentralappointmentbooking.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private EditText emailInput;
    private EditText userNameInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private EditText newPasswordInput;
    private EditText newPasswordConfirmInput;

    private String originalPhone;
    private String originalUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailInput = findViewById(R.id.emailInput);
        userNameInput = findViewById(R.id.userNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        newPasswordConfirmInput = findViewById(R.id.newPasswordConfirmInput);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(Util.userUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            emailInput.setText(user.email);
                            userNameInput.setText(user.userName);
                            phoneInput.setText(user.phone);
                            originalUserName = user.userName;
                            originalPhone = user.phone;
                        }
                    } else {
                        Toast.makeText(this,
                                "Felhasználói adatok nem találhatók!",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Hiba a Firestore lekérdezéskor: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());

        // Menu
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
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
                finish();
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
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(Util.userUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        userNameInput.setText(user.userName);
                        phoneInput.setText(user.phone);
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    public void saveProfile(View view) {

        String email = emailInput.getText().toString().trim();

        String userName = userNameInput.getText().toString().trim();
        if (!Validator.isValidUsername(userName)) {
            userNameInput.setError("Érvénytelen felhasználónév!");
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

        String newPassword = newPasswordInput.getText().toString().trim();
        boolean changingPassword = !newPassword.isEmpty();

        if (changingPassword) {
            if (!Validator.isValidPassword(newPassword)) {
                newPasswordInput.setError("Érvénytelen az új jelszó!\n" + Validator.PASSWORD_ROULE);
                return;
            }

            String newPasswordConfirm = newPasswordConfirmInput.getText().toString().trim();
            if (!newPassword.equals(newPasswordConfirm)) {
                newPasswordConfirmInput.setError("A két új jelszó nem egyezik!");
                return;
            }
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }

        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (changingPassword) {
                            if (!password.equals(newPassword)) {
                                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                        .updatePassword(newPassword)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Jelszó sikeresen módosítva",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUserData(userName, phone);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(
                                                this, "Jelszó módosítás sikertelen: " +
                                                        e.getMessage(), Toast.LENGTH_LONG).show());
                            } else {
                                Toast.makeText(this, "A jelszó módosítása szükségtelen!",
                                        Toast.LENGTH_SHORT).show();
                                updateUserData(userName, phone);
                            }
                        } else {
                            updateUserData(userName, phone);
                        }
                    } else {
                        Toast.makeText(this,
                                "Hibás jelszó!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUserData(String userName, String phone) {
        if (originalPhone.equals(phone) && userName.equals(originalUserName)) {
            Toast.makeText(this, "A profilt nem szükséges frissíteni!",
                    Toast.LENGTH_SHORT).show();
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
            finish();
            return;
        }
        User updatedUser = new User(userName, emailInput.getText().toString().trim(), phone);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(Util.userUid)
                .set(updatedUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profil sikeresen frissítve!", Toast.LENGTH_SHORT).show();
                    Util.userName = userName;
                    overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this, "Hiba a frissítéskor: " +
                                e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
