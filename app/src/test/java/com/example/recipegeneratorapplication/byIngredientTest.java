package com.example.recipegeneratorapplication;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class byIngredientTest {
    @Test
    public void byIngredientTesting() {
        String API_KEY = BuildConfig.SPOONACULAR_API_KEY;
        ArrayList<RecipeSummary> listResult = new ArrayList<>();

        ArrayList<RecipeSummary> listResults = new ArrayList<>();
        try {

            //creates string with entire url to search using HttpURLConnection
            String spoonacularUrl = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=" + API_KEY +
                    "&ingredients=" + "potato" + "&number=30";

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

        // Check if listResult is NULL (has no data)
        assertNotNull(listResult);
    }
}