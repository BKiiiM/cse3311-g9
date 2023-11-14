package com.example.recipegeneratorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainMenuActivity extends AppCompatActivity
{

    Button search_name_button;
    Button search_ingredient_button;
    TextView displaySaved;
    private TextView tipOfTheDayTextView;
    private List<String> tips;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Initialize buttons using xml ids
        search_ingredient_button = (Button) findViewById(R.id.ingredient_1_input);
        search_name_button = (Button) findViewById(R.id.search_by_name);
        displaySaved = (TextView) findViewById(R.id.saved_recipes);
        // Set click listener to navigate to the SearchByNameActivity
        search_name_button.setOnClickListener(this::goToNameSearch);
        // Set click listener to navigate to the SearchByIngredientsActivity
        search_ingredient_button.setOnClickListener(this::goToIngredientSearch);
        displaySaved.setOnClickListener(this::goToSavedRecipes);

        tipOfTheDayTextView = findViewById(R.id.tip_of_the_day);

        // Read tips from the "cookingtips.txt" file located in res/raw/ and store them in the tips list.
        tips = readTipsFromRawResource(R.raw.cookingtips);

        displayRandomTipOfTheDay();

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

    private void goToSavedRecipes(View view)
    {
        Intent myIntent = new Intent(MainMenuActivity.this, SavedRecipesActivity.class);
        MainMenuActivity.this.startActivity(myIntent);
    }

    private List<String> readTipsFromRawResource(int resourceId) {
        List<String> tipList = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(resourceId);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                tipList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tipList;
    }
    private void displayRandomTipOfTheDay() {
        if (!tips.isEmpty()) {
            int randomIndex = new Random().nextInt(tips.size());
            String randomTip = tips.get(randomIndex);
            tipOfTheDayTextView.setText(randomTip);
        }
    }

}