package com.example.recipegeneratorapplication;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    TextView selectedRecipeCalories;
    TextView selectedRecipePrepTime;
    Spinner RecipeServingSize;
    int servingSize;
    JSONObject recipeData;
    ArrayAdapter<String> spinnerAdapter;


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
                //we add to the string includeNutrition=true to get calories,servings and
                //preparation time information from spoonacular API
                String spoonacularUrl = "https://api.spoonacular.com/recipes/" + id + "/information" +
                        "?includeNutrition=true&apiKey=" + API_KEY;
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
        protected void onPostExecute(JSONObject parsedObjectResult)
        {
            super.onPostExecute(parsedObjectResult);
           //spinner is updated with serving size from api call
            try
            {
                displaySpinnerServingSize(parsedObjectResult);
                //set serving size according to api return information
                servingSize = parsedObjectResult.getInt("servings");
            } catch (JSONException e)
            {
                throw new RuntimeException(e);
            }

            displayRecipeInformation(parsedObjectResult);
            recipeData = parsedObjectResult;


        }
    }
    private void displayRecipeInformation(JSONObject result)
    {
        //updating UI with data from results of JSONObject all fields from API are set here
        //recipe name, photo, number of servings, calories, cooking time
        //ingredients and instructions
        try
        {
            selectedRecipeTitle.setText(result.getString("title"));
            displayIngredients(result);
            displayInstructions(result);
            displayCalories(result);
            displayPrepTime(result);


            //Picasso extracts the recipe photo contained in the field "image"
            // and displays it on the screen using the ImageView widget
            Picasso.get().load(result.getString("image")).into(selectedRecipePhoto);

        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void displayPrepTime(JSONObject result) throws JSONException
    {
        int prepTime = result.getInt("readyInMinutes");
        selectedRecipePrepTime.setText(prepTime+" Minutes");
    }
    private void displaySpinnerServingSize(JSONObject result) throws JSONException
    {
        int numberOfServings = result.getInt("servings");
        RecipeServingSize.setSelection(numberOfServings-1);
    }
    private void displayCalories(JSONObject result)throws JSONException
    {
        JSONObject nutritionObject = result.getJSONObject("nutrition");
        JSONArray nutrientsArray = nutritionObject.getJSONArray("nutrients");
        JSONObject calorieObject = nutrientsArray.getJSONObject(0);
        double calorieCount = calorieObject.getDouble("amount");
        String totalCalories = Double.toString(calorieCount);
        selectedRecipeCalories.setText(totalCalories+" Kcal");

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
        int apiServingSize = result.getInt("servings");
        // get array of objects extendedIngredients so we can access the field "original"
        // "original" contains each ingredient needed for the recipe
        JSONArray extendedIngredientsArr = result.getJSONArray("extendedIngredients");
        String selectedIngredients = "";
        String allSelectedIngredients = "";
        for(int i= 0; i< extendedIngredientsArr.length();i++)
        {
            String ingredientName;
            String ingredientUnit;
            double ingredientAmount;
            double formatIngredientAmount;

            JSONObject extendedIngredientElement = extendedIngredientsArr.getJSONObject(i);
            ingredientName = extendedIngredientElement.getString("name");
            ingredientUnit = extendedIngredientElement.getString("unit");
            ingredientAmount = extendedIngredientElement.getDouble("amount");

            //adapts the ingredient amount to the user requested serving size amount
            ingredientAmount = (ingredientAmount * servingSize)/apiServingSize;
            formatIngredientAmount = Double.parseDouble(formatDouble(ingredientAmount));
            selectedIngredients = formatIngredientAmount +" " + ingredientUnit + " " + ingredientName;

            allSelectedIngredients = allSelectedIngredients + selectedIngredients+"\n";

        }
        // display the ingredients from the selected recipe on the screen
        selectedRecipeIngredients.setText(allSelectedIngredients);
    }
    private String formatDouble(double value) {
        // Use DecimalFormat to format the double with one decimal place
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(value);
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
        selectedRecipeCalories = findViewById(R.id.TextView_calories);
        selectedRecipePrepTime = findViewById(R.id.TextView_preparation_time);
        selectedRecipeIngredients = findViewById(R.id.selected_recipe_ingredients);
        selectedRecipeInstructions = findViewById(R.id.selected_recipe_instructions);

        RecipeServingSize = findViewById(R.id.serving_size_spinner);
        ArrayList<String> spinnerOptions = new ArrayList<String>();
        int counter;
        for(counter = 0; counter< 10; counter++)
        {
            if (counter==0)
            {
                spinnerOptions.add(counter+1+" Serving");
            }
            else
                spinnerOptions.add(counter+1 +" Servings");
        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        RecipeServingSize.setAdapter(spinnerAdapter);
        RecipeServingSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(recipeData != null)
                {
                    //set serving size according to user choice
                    servingSize = position+1;
                    displayRecipeInformation(recipeData);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        Button favoriteButton = findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(v -> {
            if (favoriteButton.getText().equals("Favorite")) {
                // User wants to save the recipe as a favorite
                saveRecipeToFirebase(recipeId);
                favoriteButton.setText("Unfavorite");
                Toast.makeText(DisplayRecipeSelected.this, "Recipe saved to favorites!", Toast.LENGTH_LONG).show();
            } else {
                // User wants to remove the recipe from favorites
                unfavoriteRecipe(recipeId);
                favoriteButton.setText("Favorite");
                Toast.makeText(DisplayRecipeSelected.this, "Recipe removed from favorites!", Toast.LENGTH_LONG).show();
            }
        });

        new CheckFavoriteRecipeTask().execute(recipeId);

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
        favoriteRecipesRef.child(Integer.toString(recipeId)).setValue(true);

        // Show a message to indicate that the recipe is now a favorite
        Toast.makeText(DisplayRecipeSelected.this, "Recipe saved to favorites!", Toast.LENGTH_LONG).show();
    }


    private void unfavoriteRecipe(int recipeId) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoriteRecipesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favoriteRecipes");
        favoriteRecipesRef.child(Integer.toString(recipeId)).removeValue();
    }

    private boolean isRecipeFavorited(int recipeId) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoriteRecipesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favoriteRecipes").child(Integer.toString(recipeId));

        try {
            DataSnapshot dataSnapshot = Tasks.await(favoriteRecipesRef.get()); // Use the get method to fetch the data
            Boolean isFavorited = dataSnapshot.getValue(Boolean.class);

            return isFavorited != null && isFavorited;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private class CheckFavoriteRecipeTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference favoriteRecipesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favoriteRecipes").child(Integer.toString(params[0]));

            try {
                DataSnapshot dataSnapshot = Tasks.await(favoriteRecipesRef.get());
                Boolean isFavorited = dataSnapshot.getValue(Boolean.class);
                return isFavorited != null && isFavorited;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isFavorited) {
            Button favoriteButton = findViewById(R.id.favorite_button);
            if (isFavorited) {
                favoriteButton.setText("Unfavorite");
            } else {
                favoriteButton.setText("Favorite");
            }
        }
    }

}