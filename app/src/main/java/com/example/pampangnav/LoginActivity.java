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

public class LoginActivity extends AppCompatActivity {

    EditText emailLogin, passwordLogin;
    RadioGroup loginRoleGroup;
    TextView SignupTxt;
    Button loginButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginRoleGroup = findViewById(R.id.loginRoleGroup);
        loginButton = findViewById(R.id.loginButton);
        SignupTxt = findViewById(R.id.SignupTxt);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();




        SignupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(LoginActivity.this, signupactivity.class);
                startActivity(signup);
            }
        });


        loginButton.setOnClickListener(v -> loginUser());
    }


    private void loginUser() {
        String emailTxt = emailLogin.getText().toString().trim();
        String passTxt = passwordLogin.getText().toString();

        int selectedRoleId = loginRoleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        String role = ((RadioButton) findViewById(selectedRoleId)).getText().toString().toLowerCase();

        if (emailTxt.isEmpty() || passTxt.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(emailTxt, passTxt).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();

                db.collection(role + "s").document(uid).get()
                        .addOnSuccessListener(document -> {
                            if (document.exists()) {
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                if (role.equals("buyer")) {
                                    startActivity(new Intent(this, BuyerActivity.class));
                                } else {
                                    startActivity(new Intent(this, SellerActivity.class));
                                }
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "This user is not registered as " + role, Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            } else {
                Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


