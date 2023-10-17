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

public class byNameTest {
    @Test
    public void byNameTesting() {
        String API_KEY = BuildConfig.SPOONACULAR_API_KEY;
        ArrayList<RecipeSummary> result = new ArrayList<>();

        try {
            //creates string with entire url to search using HttpURLConnection
            String spoonacularUrl = "https://api.spoonacular.com/recipes/complexSearch" +
                    "?apiKey=" + API_KEY +
                    "&query=" + "spaghetti" +
                    "&number=30"; //number of results shown, we can change this number

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

        // Check if result is NULL (has no data)
        assertNotNull(result);
    }
}