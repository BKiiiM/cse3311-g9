package com.example.recipegeneratorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase for authentication
        FirebaseApp.initializeApp(this);

        // Find the "Don't have an account? Sign in" TextView
        TextView noAccountText = findViewById(R.id.NoAccount);

        // Set a click listener on the login button
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the method to handle login
                handleLoginDialog();
            }
        });

        // Set a click listener on the TextView for registration
        noAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate to the registration activity
                Intent registrationIntent = new Intent(LoginActivity.this, RegistrationActivity.class);

                // Start the registration activity
                startActivity(registrationIntent);
            }
        });
    }

    // Method to handle the login process
    private void handleLoginDialog() {
        // Find the email and password input fields
        EditText emailEdit = findViewById(R.id.email);
        EditText passwordEdit = findViewById(R.id.password);

        // Initialize Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Attempt to sign in with the provided email and password
        mAuth.signInWithEmailAndPassword(emailEdit.getText().toString(), passwordEdit.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(LoginActivity.this, "Login successful",
                                Toast.LENGTH_LONG).show();

                        // Navigate to another activity upon successful login
                        next_screen();
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this,
                                "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // This method navigates to the main menu screen after login is successful
    private void next_screen() {
        Intent myIntent = new Intent(LoginActivity.this, MainMenuActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }
}
