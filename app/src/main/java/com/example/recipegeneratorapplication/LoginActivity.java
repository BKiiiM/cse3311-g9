package com.example.recipegeneratorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.GoogleIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

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

//                //TEMPORARY CODE TO BYPASS LOGIN while fixing the Main Menu Activity
//                Intent myIntent = new Intent(LoginActivity.this, MainMenuActivity.class);
//                LoginActivity.this.startActivity(myIntent);
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
        finish();
        Intent myIntent = new Intent(LoginActivity.this, MainMenuActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    private void signInGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                next_screen();
            } catch (ApiException e){
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }

    }
}