package com.example.recipegeneratorapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

public class MainMenuActivity extends AppCompatActivity {

    Button search_button;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        search_button = (Button) findViewById(R.id.search_by_name);

        search_button.setOnClickListener(this::next_screen);

    }
    private void next_screen(View view)
    {
        Intent myIntent = new Intent(MainMenuActivity.this, search_by_name_activity.class);
        MainMenuActivity.this.startActivity(myIntent);
    }

}