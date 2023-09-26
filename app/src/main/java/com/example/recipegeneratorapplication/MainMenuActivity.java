package com.example.recipegeneratorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    Button search_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Initialize the search button
        search_button = (Button) findViewById(R.id.search_by_name);

        // Set a click listener to navigate to the SearchByNameActivity
        search_button.setOnClickListener(this::next_screen);

    }

    // This method navigates to the SearchByNameActivity
    private void next_screen(View view)
    {
        Intent myIntent = new Intent(MainMenuActivity.this, SearchByNameActivity.class);
        MainMenuActivity.this.startActivity(myIntent);
    }

}