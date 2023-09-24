package com.example.recipegeneratorapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class search_by_name_activity extends AppCompatActivity
{
    EditText input_recipe_name;

    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);

        //create a reference to input text box for recipe name

        input_recipe_name = findViewById(R.id.recipe_name_input_box);

        button = findViewById(R.id.search_name_button);

        //when search button is clicked the method search_recipes_by_name is called
        //use this if onClick is not used and we want to set our buttons dynamically,
        //both methods are equivalent

        button.setOnClickListener(this::search_recipes_by_name);

    }

    public void search_recipes_by_name(View v)
    {

        Log.d("message", "Have a great day!");


    }

    //
}