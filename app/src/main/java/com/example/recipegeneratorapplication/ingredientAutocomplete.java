package com.example.recipegeneratorapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.example.recipegeneratorapplication.BuildConfig;

public class ingredientAutocomplete extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;
    private EditText autocompleteSearchQueryEditText;
    private ListView autocompleteSearchResultsListView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_autocomplete);

        autocompleteSearchQueryEditText = findViewById(R.id.autocompleteSearchQueryEditText);
        autocompleteSearchResultsListView = findViewById(R.id.autocompleteSearchResultsListView);

        // Create an adapter for the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        autocompleteSearchResultsListView.setAdapter(adapter);

        // Add a text change listener to the EditText
        autocompleteSearchQueryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // When the text changes, trigger the API call
                String query = charSequence.toString();
                new ingredientAutocomplete2().execute(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private class ingredientAutocomplete2 extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String query = params[0];

            // Create the Spoonacular API request URL.
            String spoonacularUrl = "https://api.spoonacular.com/food/ingredients/autocomplete" +
                    "?apiKey=" + API_KEY +
                    "&query=" + query +
                    "&number=10";

            // Make the Spoonacular API request.
            try {
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

                    // Parse the JSON response from the Spoonacular API.
                    JSONArray jsonArray = new JSONArray(response.toString());

                    // Extract the ingredient names from the results.
                    ArrayList<String> ingredientNames = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ingredient = jsonArray.getJSONObject(i);
                        String ingredientName = ingredient.getString("name"); // Use the correct key
                        ingredientNames.add(ingredientName);
                    }

                    return ingredientNames;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> ingredientNames) {
            if (ingredientNames != null) {
                // Update the adapter with the new data
                adapter.clear();
                adapter.addAll(ingredientNames);
            }
        }
    }
}
