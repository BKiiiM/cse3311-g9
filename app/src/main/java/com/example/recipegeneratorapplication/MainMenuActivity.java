package com.example.recipegeneratorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    Button search_name_button;
    Button search_ingredient_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Initialize buttons using xml ids
        search_ingredient_button = (Button) findViewById(R.id.recipe_ingredients_input);
        search_name_button = (Button) findViewById(R.id.search_by_name);
        // Set click listener to navigate to the SearchByNameActivity
        search_name_button.setOnClickListener(this::goToNameSearch);
        // Set click listener to navigate to the SearchByIngredientsActivity
        search_ingredient_button.setOnClickListener(this::goToIngredientSearch);

    }

    // This method navigates to the SearchByNameActivity
    private void goToNameSearch(View view)
    {
        Intent myIntent = new Intent(MainMenuActivity.this, SearchByNameActivity.class);
        MainMenuActivity.this.startActivity(myIntent);
    }
    //this method navigates to the SearchByIngredientsActivity
    private void goToIngredientSearch(View view)
    {
        Intent myIntent = new Intent(MainMenuActivity.this, SearchByIngredientsActivity.class);
        MainMenuActivity.this.startActivity(myIntent);
    }

}