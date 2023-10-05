package com.example.recipegeneratorapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;


public class SearchByIngredientsActivity extends AppCompatActivity
{
    private static final String API_KEY = "7bc4cc04deb34da4b63e486dd734ca93";
    Button searchByIngredientButton;
    EditText inputRecipeIngredients;
    ListView resultListByIngredients;
    ArrayAdapter<RecipeSummary> adapterView;
    ArrayList<RecipeSummary> listByIngredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_ingredients);

        //create references for search button, input textbox and list of results
        searchByIngredientButton =findViewById(R.id.search_ingredients);
        inputRecipeIngredients =findViewById(R.id.recipe_ingredients_input);
        resultListByIngredients = findViewById(R.id.result_search_ingredients_ListView);

        //setup an OnClickListener to get the user input text
        searchByIngredientButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get the user input from the inputRecipeByIngredients EditText box
                // when it's used and store it in a String
                String userInput = inputRecipeIngredients.getText().toString();
                // Now 'userInput' contains the user-entered text as a String

                //Test String
                String[] testData = userInput.split(",");
                printInputString(testData);

            }
        });





    }
    public void printInputString(String[] testData)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, testData);
        resultListByIngredients.setAdapter(adapter);
    }
}











