package com.example.recipegeneratorapplication;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.gson.annotations.JsonAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DisplayRecipeSelected extends AppCompatActivity
{
    TextView selectedRecipeId;
    TextView selectedRecipeTitle;
    ImageView selectedRecipePhoto;
    TextView selectedRecipeIngredients;
    TextView selectedRecipeInstructions;
    int recipeId;
    private FirebaseAuth mAuth;

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
            // contains all the recipe information
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
                //Picasso extracts the recipe photo contained in the field "image"
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

        // On Spoonacular "analyzedInstructions" contains an array of objects, we're interested in
        // the first object at index 0
        // this first object contains another array of objects called "steps"
        // we're interested in extracting the field "step" from the array "steps"
        // This field contains the recipe instructions for the recipe clicked on by the user
        JSONArray recipeInstructions = result.getJSONArray("analyzedInstructions");
        JSONObject analyzedInstructions = recipeInstructions.getJSONObject(0);
        JSONArray stepsArray = analyzedInstructions.getJSONArray("steps");
        int recipe_number;
        String recipe_step = "";
        String recipe_all_steps = "";
        for (int i = 0; i < stepsArray.length(); i++)
        {
            JSONObject step = stepsArray.getJSONObject(i);
            // we also access the field "number" to extract the number of the step to follow
            // so we can organize our screen output better
            recipe_number = step.getInt("number");
            recipe_step = step.getString("step");
            recipe_all_steps = recipe_all_steps+"Step "+recipe_number+") "+recipe_step+"\n\n";

        }
        // output the recipe step number and instructions to screen
        selectedRecipeInstructions.setText(recipe_all_steps);
    }

    private void displayIngredients(JSONObject result) throws JSONException
    {
        // get array of objects extendedIngredients so we can access the field "original"
        // "original" contains each ingredient needed for the recipe
        JSONArray extendedIngredientsArr = result.getJSONArray("extendedIngredients");
        String selectedIngredients = "";
        String allSelectedIngredients = "";
        for(int i=0; i< extendedIngredientsArr.length();i++)
        {
            JSONObject extendedIngredientElement = extendedIngredientsArr.getJSONObject(i);
            selectedIngredients = extendedIngredientElement.getString("original");
            allSelectedIngredients = allSelectedIngredients+selectedIngredients+"\n";

        }
        // display the ingredients from the selected recipe on the screen
        selectedRecipeIngredients.setText(allSelectedIngredients);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe_selected);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth == null)
        {
            Toast.makeText(DisplayRecipeSelected.this, "mAuth is null", Toast.LENGTH_LONG).show();
        }
        else if(mAuth.getCurrentUser() == null)
        {
            Toast.makeText(DisplayRecipeSelected.this, "getCurrentUser is null", Toast.LENGTH_LONG).show();
        }

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);
        setRecipeId(id);
        selectedRecipeId = findViewById(R.id.recipe_API_id);
        selectedRecipeTitle = findViewById(R.id.recipe_title);
        selectedRecipePhoto = findViewById(R.id.recipe_photo);
        selectedRecipeIngredients = findViewById(R.id.selected_recipe_ingredients);
        selectedRecipeInstructions = findViewById(R.id.selected_recipe_instructions);

        Button favoriteButton = findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                // User is not signed in. Prompt the user to sign in.
                Toast.makeText(DisplayRecipeSelected.this, "Please sign in to save recipes as favorites", Toast.LENGTH_LONG).show();
            } else {
                // User is signed in. Save the recipe to Firebase.
                saveRecipeToFirebase(recipeId);
            }
        });


        //displays the recipe id on screen just to test the function
        // selectedRecipeId.setText(id+"");
        // selectedRecipeIngredients.setText("");
        new recipeSearchById().execute(id);


    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }


    private void saveRecipeToFirebase(int recipeId) {
        String userId = mAuth.getCurrentUser().getUid().toString();

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favoriteRecipesRef = database.getReference("users").child(userId).child("favoriteRecipes");

        // Save the recipe ID under the user's "favoriteRecipes" node
        favoriteRecipesRef.child("recipe" + recipeId).setValue(true);

        // Show a message to indicate that the recipe is now a favorite
        Toast.makeText(DisplayRecipeSelected.this, "Recipe saved to favorites!", Toast.LENGTH_LONG).show();
    }







}