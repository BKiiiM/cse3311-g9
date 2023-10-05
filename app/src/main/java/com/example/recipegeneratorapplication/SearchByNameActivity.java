package com.example.recipegeneratorapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchByNameActivity extends AppCompatActivity
{
    private static final String API_KEY = "7bc4cc04deb34da4b63e486dd734ca93";
    EditText inputRecipeName;
    Button searchButton;
    ListView resultListByName;
    ArrayAdapter<RecipeSummary> adapter;
    ArrayList<RecipeSummary> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);

        //create references to input text box, button and list view
        inputRecipeName = findViewById(R.id.recipe_name_input_box);
        searchButton = findViewById(R.id.search_name_button);
        resultListByName = findViewById(R.id.result_recipe_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        resultListByName.setAdapter(adapter);

        resultListByName.setOnItemClickListener(this::onItemClick);

        searchButton.setOnClickListener(v -> {
            String query = inputRecipeName.getText().toString().trim();
            if (!query.isEmpty())
            {
                new recipeSearchByName().execute(query);
            }
        });
    }

    //we need to know the index of the recipe selected by the user
    //to extract information from the recipe
    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        RecipeSummary recipeData = recipeList.get(i);
        int recipeId = recipeData.getId();

        Intent myIntent = new Intent(SearchByNameActivity.this, DisplayRecipeSelected.class);
        myIntent.putExtra("id", recipeId);
        SearchByNameActivity.this.startActivity(myIntent);

    }


    private class recipeSearchByName extends AsyncTask<String, Void, ArrayList<RecipeSummary>> {
        @Override
        protected ArrayList<RecipeSummary> doInBackground(String... params) {
            String query = params[0];
            ArrayList<RecipeSummary> result = new ArrayList<>();

            try {
                //creates string with entire url to search using HttpURLConnection
                String spoonacularUrl = "https://api.spoonacular.com/recipes/complexSearch" +
                        "?apiKey=" + API_KEY +
                        "&query=" + query +
                        "&number=20"; //we can change this number

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

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray results = jsonObject.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject recipe = results.getJSONObject(i);
                        String recipeName = recipe.getString("title");
                        int id = recipe.getInt("id");
                        RecipeSummary recipeData = new RecipeSummary();
                        recipeData.setId(id);
                        recipeData.setTitle(recipeName);
                        result.add(recipeData);
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<RecipeSummary> result) {
            super.onPostExecute(result);
            recipeList.clear();
            recipeList.addAll(result);
            adapter.notifyDataSetChanged();
        }
    }
}
