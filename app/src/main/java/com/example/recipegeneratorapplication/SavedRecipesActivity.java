package com.example.recipegeneratorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SavedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<RecipeSummary> savedRecipes;
    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Hello", "in oNCreate method");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);


        recyclerView = findViewById(R.id.recyclerView); // Initialize the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(SavedRecipesActivity.this));

        savedRecipes = new ArrayList<>();
        adapter = new RecipeAdapter(SavedRecipesActivity.this, savedRecipes, this);
        recyclerView.setAdapter(adapter);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            Log.d("Hello", "in if statement");

            String userId = user.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Replace with your database reference path

            // Reference to the user's node in the database
            DatabaseReference userReference = databaseReference.child(userId).child("favoriteRecipes");
            Log.d("Hello", "userreference: " + userReference);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> savedRecipeIds = new ArrayList<>();
                    Log.d("Hello", "in onDataChange statement");


                    // Loop through the data to get the saved recipe IDs.
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        String recipeId = recipeSnapshot.getKey();
                        boolean isSaved = recipeSnapshot.getValue(Boolean.class);
                        if (isSaved) {
                            savedRecipeIds.add(recipeId);
                        }
                    }

                    // Retrieve the recipe data for each ID and update your savedRecipes list.
                    for (String recipeId : savedRecipeIds) {
                        getRecipeInfo(recipeId);
                    }
                    Log.d("Hello", "right before adapter");

                }



                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Hello", "in onCancelled");
                }
            });
        } else {
            // Handle the case when the user is not logged in.
        }

    }

    @Override
    public void onRecipeClick(RecipeSummary recipe) {
        // Handle the click event when a recipe card is clicked
        Intent intent = new Intent(SavedRecipesActivity.this, DisplayRecipeSelected.class);
        intent.putExtra("id", recipe.getId());
        startActivity(intent);
    }

    private class FetchRecipeTask extends AsyncTask<String, Void, RecipeSummary> {
        @Override
        protected RecipeSummary doInBackground(String... params) {
            String recipeId = params[0];
            RecipeSummary recipeData = new RecipeSummary();

            try {

                Log.d("Hello", "in FetchRecipeTask Try block");
                // Create the URL for the API request
                String spoonacularUrl = "https://api.spoonacular.com/recipes/" + recipeId + "/information" + "?apiKey=" + API_KEY;
                URL url = new URL(spoonacularUrl);

                // Open a connection and perform the network request
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

                    // Parse the JSON response
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String recipeName = jsonObject.getString("title");
                    int id = jsonObject.getInt("id");
                    Log.d("Hello", "JSONObject: " + jsonObject);

                    recipeData.setId(id);
                    recipeData.setTitle(recipeName);
                    recipeData.setRecipeImageURL(jsonObject.getString("image"));
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return recipeData;
        }

        @Override
        protected void onPostExecute(RecipeSummary recipeData) {
            // This method runs on the main thread and can update the UI
            savedRecipes.add(recipeData);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }


    public void getRecipeInfo(String recipeId) {
        new FetchRecipeTask().execute(recipeId);
    }






}