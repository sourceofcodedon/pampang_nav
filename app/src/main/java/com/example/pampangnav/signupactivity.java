package com.example.pampangnav;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signupactivity extends AppCompatActivity {
    EditText email, username, password, confirmPassword;
    TextView signinTxt;
    RadioGroup roleGroup;
    Button signupButton;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signupactivity);

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        roleGroup = findViewById(R.id.roleGroup);
        signupButton = findViewById(R.id.signupButton);
        signinTxt = findViewById(R.id.signinTxt);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(v -> registerUser());


        signinTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(signupactivity.this, LoginActivity.class );
                startActivity(signin);
            }
        });

    }
        private void registerUser () {
            String emailTxt = email.getText().toString().trim();
            String usernameTxt = username.getText().toString().trim();
            String passTxt = password.getText().toString().trim();
            String confirmPassTxt = confirmPassword.getText().toString().trim();

            int selectedRoleId = roleGroup.getCheckedRadioButtonId();
            if (selectedRoleId == -1) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }
            String role = ((RadioButton) findViewById(selectedRoleId)).getText().toString().toLowerCase();

            if (!passTxt.equals(confirmPassTxt)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidPassword(passTxt)) {
                Toast.makeText(this, "Password must be at least 8 characters with 1 uppercase and 1 special character", Toast.LENGTH_LONG).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(emailTxt, passTxt).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", emailTxt);
                    userData.put("username", usernameTxt);
                    userData.put("role", role);

                    db.collection(role + "s").document(uid).set(userData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(signupactivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e -> Toast.makeText(signupactivity.this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(signupactivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~-].*");
    }
}


