package com.example.recipegeneratorapplication;

//import org.junit.Test;
import org.junit.jupiter.api.Test;
//import static org.junit.Assert.*;

import android.widget.Toast;

//import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class emailTest {
    @Test
    public void emailTesting() {
        String testEmail = "testing@gmail.com";
        String testPassword = "test0123";
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(testEmail, testPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(LoginActivity.this, "Login successful",
                                Toast.LENGTH_LONG).show();
                        assertTrue(task);
                    }
                });
    }
}
