package com.example.recipegeneratorapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.SQLOutput;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        // Find the "Already have an account? Log in" TextView
        TextView alreadyHaveAccountText = findViewById(R.id.AlreadyHaveAccount);
        Button registerButton = findViewById(R.id.signup_button);

        EditText usernameEditText = findViewById(R.id.username);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        EditText confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                Log.d("RegistrationActivity", "Username: " + username);
                Log.d("RegistrationActivity", "Email: " + email);
                Log.d("RegistrationActivity", "Password: " + password);
                Log.d("RegistrationActivity", "Confirm Password: " + confirmPassword);

                Toast.makeText(RegistrationActivity.this, "Register button clicked", Toast.LENGTH_SHORT).show();

                System.out.println(username + email + password + confirmPassword);

                handleSignupDialog();
            }
        });

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


        EditText emailEdit = findViewById(R.id.email);
        EditText usernameEdit = findViewById(R.id.username);
        EditText passwordEdit = findViewById(R.id.password);
        EditText confirmPassEdit = findViewById(R.id.confirm_password);

        if(!(confirmPassEdit.getText().toString().equals(passwordEdit.getText().toString())))
        {
            Toast.makeText(RegistrationActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();

            //TODO: fix the break
            return;
        }

        User user = new User();
        user.setEmail(emailEdit.getText().toString());
        user.setUsername(usernameEdit.getText().toString());
        user.setPassword(passwordEdit.getText().toString());
        user.setVegetarian(((CheckBox) findViewById(R.id.checkbox_vegetarian)).isChecked());
        user.setVegan(((CheckBox) findViewById(R.id.checkbox_vegan)).isChecked());
        user.setGlutenFree(((CheckBox) findViewById(R.id.checkbox_gluten_free)).isChecked());

        HashMap<String, String> map = new HashMap<>();

        map.put("email", emailEdit.getText().toString());
        map.put("username", usernameEdit.getText().toString());
        map.put("password", passwordEdit.getText().toString());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailEdit.getText().toString(), passwordEdit.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference("users");
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        usersRef.child(userId).setValue(user);


                        Toast.makeText(RegistrationActivity.this,
                                "Signed up successfully", Toast.LENGTH_LONG).show();
                    } else {
                        // Registration failed
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegistrationActivity.this,
                                    "Email address already registered", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegistrationActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

}