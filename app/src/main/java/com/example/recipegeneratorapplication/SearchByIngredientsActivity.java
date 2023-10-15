package com.example.recipegeneratorapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class SearchByIngredientsActivity extends AppCompatActivity {
    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;
    AutoCompleteTextView ingredient1;
    AutoCompleteTextView ingredient2;
    AutoCompleteTextView ingredient3;
    Button searchByIngredientButton;
    ListView resultListByIngredients;
    ArrayAdapter<RecipeSummary> adapterView;
    ArrayList<RecipeSummary> listByIngredients = new ArrayList<>();
    ArrayAdapter<String> ingredientAutocompleteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_ingredients);

        //create references to input text box, button and list view
        ingredient1 = findViewById(R.id.ingredient_1_input);
        ingredient2 = findViewById(R.id.ingredient_2_input);
        ingredient3 = findViewById(R.id.ingredient_3_input);
        searchByIngredientButton = findViewById(R.id.search_ingredients);
        resultListByIngredients = findViewById(R.id.result_search_ingredients_ListView);

        adapterView = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listByIngredients);
        resultListByIngredients.setAdapter(adapterView);

        resultListByIngredients.setOnItemClickListener(this::onItemClick);

        //setup an OnClickListener to call the function onButtonClick when the
        //search button is clicked
        searchByIngredientButton.setOnClickListener(this::onButtonClick);

        ingredient1 = findViewById(R.id.ingredient_1_input);
        ingredient2 = findViewById(R.id.ingredient_2_input);
        ingredient3 = findViewById(R.id.ingredient_3_input);

        // Initialize the AutoCompleteTextView adapters
        ingredientAutocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ingredient1.setAdapter(ingredientAutocompleteAdapter);
        ingredient2.setAdapter(ingredientAutocompleteAdapter);
        ingredient3.setAdapter(ingredientAutocompleteAdapter);

        ingredient1.setThreshold(1);  // Display suggestions after typing 1 character
        ingredient2.setThreshold(1);
        ingredient3.setThreshold(1);

        ingredient1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call the autocomplete task with the current text in ingredient1
                new IngredientAutocompleteTask(ingredient1).execute(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        ingredient2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call the autocomplete task with the current text in ingredient2
                new IngredientAutocompleteTask(ingredient2).execute(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ingredient3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call the autocomplete task with the current text in ingredient3
                new IngredientAutocompleteTask(ingredient3).execute(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void onButtonClick(View view) {
        String query1 = ingredient1.getText().toString().trim();
        String query2 = ingredient2.getText().toString().trim();
        String query3 = ingredient3.getText().toString().trim();

        String all_ingredients_query = query1 + "," + query2 + "," + query3;

        if (!query1.isEmpty() || !query2.isEmpty() || !query3.isEmpty()) {
            new recipeSearchByIngredients().execute(all_ingredients_query);
        }
    }

    //we need to know the index of the recipe selected by the user
    //to extract information from the recipe
    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        RecipeSummary recipeData = listByIngredients.get(i);
        int recipeId = recipeData.getId();
        Intent myIntent = new Intent(SearchByIngredientsActivity.this, DisplayRecipeSelected.class);
        myIntent.putExtra("id", recipeId);
        SearchByIngredientsActivity.this.startActivity(myIntent);

    }

    private class recipeSearchByIngredients extends AsyncTask<String, Void, ArrayList<RecipeSummary>> {
        @Override
        protected ArrayList<RecipeSummary> doInBackground(String... params) {
            String ingredientInput = params[0];
            ArrayList<RecipeSummary> listResults = new ArrayList<>();
            try {

                //creates string with entire url to search using HttpURLConnection
                String spoonacularUrl = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=" + API_KEY +
                        "&ingredients=" + ingredientInput + "&number=30";

                URL url = new URL(spoonacularUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                //asks for response from url
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject recipe = jsonArray.getJSONObject(i);
                        int id = recipe.getInt("id");
                        String recipeName = recipe.getString("title");
                        RecipeSummary recipeData = new RecipeSummary();
                        recipeData.setId(id);
                        recipeData.setTitle(recipeName);
                        listResults.add(recipeData);
                    }

                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return listResults;


        }

        @Override
        protected void onPostExecute(ArrayList<RecipeSummary> result) {
            super.onPostExecute(result);
            listByIngredients.clear();
            listByIngredients.addAll(result);
            adapterView.notifyDataSetChanged();
        }
    }

    private class IngredientAutocompleteTask extends AsyncTask<String, Void, ArrayList<String>> {
        private AutoCompleteTextView autoCompleteTextView;

        public IngredientAutocompleteTask(AutoCompleteTextView autoCompleteTextView) {
            this.autoCompleteTextView = autoCompleteTextView;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String query = params[0];
            ArrayList<String> ingredientNames = new ArrayList<>();

            try {
                String spoonacularUrl = "https://api.spoonacular.com/food/ingredients/autocomplete" +
                        "?apiKey=" + API_KEY +
                        "&query=" + query +
                        "&number=10";

                URL url = new URL(spoonacularUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ingredient = jsonArray.getJSONObject(i);
                        String ingredientName = ingredient.getString("name");
                        ingredientNames.add(ingredientName);
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ingredientNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> ingredientNames) {
            if (ingredientNames != null) {
                ingredientAutocompleteAdapter.clear();
                ingredientAutocompleteAdapter.addAll(ingredientNames);
            }
        }

    }
}

