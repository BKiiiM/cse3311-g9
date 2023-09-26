package com.example.recipegeneratorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Find UI elements
        TextView alreadyHaveAccountText = findViewById(R.id.AlreadyHaveAccount);
        Button registerButton = findViewById(R.id.signup_button);
        EditText usernameEditText = findViewById(R.id.username);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        EditText confirmPasswordEditText = findViewById(R.id.confirm_password);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                // Log user input for debugging
                Log.d("RegistrationActivity", "Username: " + username);
                Log.d("RegistrationActivity", "Email: " + email);
                Log.d("RegistrationActivity", "Password: " + password);
                Log.d("RegistrationActivity", "Confirm Password: " + confirmPassword);

                Toast.makeText(RegistrationActivity.this, "Register button clicked", Toast.LENGTH_SHORT).show();

                // Call the method to handle user registration
                handleSignupDialog();
            }
        });

        // Already have an account text click listener
        alreadyHaveAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the LoginActivity
                Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);

                // Start the LoginActivity
                startActivity(loginIntent);
            }
        });
    }

    private void handleSignupDialog() {
        // Find UI elements
        EditText emailEdit = findViewById(R.id.email);
        EditText usernameEdit = findViewById(R.id.username);
        EditText passwordEdit = findViewById(R.id.password);
        EditText confirmPassEdit = findViewById(R.id.confirm_password);

        // Check if passwords match
        if (!(confirmPassEdit.getText().toString().equals(passwordEdit.getText().toString()))) {
            Toast.makeText(RegistrationActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();

            // TODO: Fix the break (consider explaining what needs fixing)
            return;
        }

        // Create a User object and populate it
        User user = new User();
        user.setEmail(emailEdit.getText().toString());
        user.setUsername(usernameEdit.getText().toString());
        user.setPassword(passwordEdit.getText().toString());
        user.setVegetarian(((CheckBox) findViewById(R.id.checkbox_vegetarian)).isChecked());
        user.setVegan(((CheckBox) findViewById(R.id.checkbox_vegan)).isChecked());
        user.setGlutenFree(((CheckBox) findViewById(R.id.checkbox_gluten_free)).isChecked());

        // Create a HashMap to store user data
        HashMap<String, String> map = new HashMap<>();
        map.put("email", emailEdit.getText().toString());
        map.put("username", usernameEdit.getText().toString());
        map.put("password", passwordEdit.getText().toString());

        // Initialize Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailEdit.getText().toString(), passwordEdit.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference("users");
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        usersRef.child(userId).setValue(user);

                        // Show a success toast message
                        Toast.makeText(RegistrationActivity.this,
                                "Signed up successfully", Toast.LENGTH_LONG).show();
                    } else {
                        // Registration failed
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            // Show a toast message for email already registered
                            Toast.makeText(RegistrationActivity.this,
                                    "Email address already registered", Toast.LENGTH_LONG).show();
                        } else {
                            // Show a toast message for registration failure with error details
                            Toast.makeText(RegistrationActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
