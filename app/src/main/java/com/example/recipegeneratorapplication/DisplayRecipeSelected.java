package com.example.recipegeneratorapplication;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.annotations.JsonAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisplayRecipeSelected extends AppCompatActivity
{
    TextView selectedRecipeId;
    TextView selectedRecipeTitle;
    ImageView selectedRecipePhoto;
    TextView selectedRecipeIngredients;
    TextView selectedRecipeInstructions;
    private static final String API_KEY = "7bc4cc04deb34da4b63e486dd734ca93";

    private class recipeSearchById extends AsyncTask<Integer, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Integer... params)
        {
            int id = params[0];
            JSONObject jsonObject = null;

            try
            {
                //creates url string for Spoonacular search
                String spoonacularUrl = "https://api.spoonacular.com/recipes/" +id+"/information"+
                        "?apiKey=" + API_KEY;
                //creates a pointer to the Spoonacular database host site
                URL url = new URL(spoonacularUrl);
                //requests http connection using method "GET"
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //asks for response from Spoonacular url and checks status
                int responseCode = connection.getResponseCode();
                //checks if the response status code is 200(ok)
                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    //once status is ok, read the entire recipe and store it in a String object
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }
                    reader.close();

                    jsonObject = new JSONObject(response.toString());

                }
                connection.disconnect();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject result)
        {
            super.onPostExecute(result);
            //updating UI with data from results of JSONObject all fields from API are set here
            //recipe name, photo, ingredients and instructions
            try
            {
                selectedRecipeTitle.setText(result.getString("title"));
                displayIngredients(result);
                displayInstructions(result);
                //Picasso extracts the recipe photo contained in tje Json object result
                // and displays it on the screen using the ImageView widget
                Picasso.get().load(result.getString("image")).into(selectedRecipePhoto);

            }
            catch (JSONException e)
            {
                throw new RuntimeException(e);
            }

        }
    }
    private void displayInstructions(JSONObject result) throws JSONException
    {

        JSONArray recipeInstructions = result.getJSONArray("analyzedInstructions");
        JSONObject analyzedInstructions = recipeInstructions.getJSONObject(0);
        JSONArray stepsArray = analyzedInstructions.getJSONArray("steps");
        int recipe_number;
        String recipe_step = "";
        String recipe_all_steps = "";
        for (int i = 0; i < stepsArray.length(); i++)
        {
            JSONObject step = stepsArray.getJSONObject(i);
            recipe_number = step.getInt("number");
            recipe_step = step.getString("step");
            recipe_all_steps = recipe_all_steps+"Step "+recipe_number+")"+recipe_step+"\n\n";

        }
        selectedRecipeInstructions.setText(recipe_all_steps);
    }

    private void displayIngredients(JSONObject result) throws JSONException
    {
        JSONArray extendedIngredientsArr = result.getJSONArray("extendedIngredients");
        String selectedIngredients = "";
        String allSelectedIngredients = "";
        for(int i=0; i< extendedIngredientsArr.length();i++)
        {
            JSONObject extendedIngredientElement = extendedIngredientsArr.getJSONObject(i);
            selectedIngredients = extendedIngredientElement.getString("original");
            allSelectedIngredients = allSelectedIngredients+selectedIngredients+"\n";

        }
        selectedRecipeIngredients.setText(allSelectedIngredients);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe_selected);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);
        selectedRecipeId = findViewById(R.id.recipe_API_id);
        selectedRecipeTitle = findViewById(R.id.recipe_title);
        selectedRecipePhoto = findViewById(R.id.recipe_photo);
        selectedRecipeIngredients = findViewById(R.id.selected_recipe_ingredients);
        selectedRecipeInstructions = findViewById(R.id.selected_recipe_instructions);

        //displays the recipe id on screen
        selectedRecipeId.setText(id+"");
        selectedRecipeIngredients.setText("");
        new recipeSearchById().execute(id);
    }


}