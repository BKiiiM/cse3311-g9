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
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeHistory extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    // Declare member variables
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<RecipeSummary> recipeHistory;
    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_history);

        recyclerView = findViewById(R.id.recyclerView); // Initialize the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(RecipeHistory.this));

        recipeHistory = new ArrayList<>();
        adapter = new RecipeAdapter(RecipeHistory.this, recipeHistory, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // If a user is logged in
            String userId = user.getUid();

            // Get a reference to the Firebase database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Replace with your database reference path

            // Reference to the user's node in the database for recipe history
            DatabaseReference historyReference = databaseReference.child(userId).child("recipeHistory");

            historyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called when data changes in the database
                    List<String> recipeIds = new ArrayList<>();

                    // Loop through the data to get the recipe IDs.
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        String recipeId = String.valueOf(recipeSnapshot.getValue());
                        recipeIds.add(recipeId);
                    }

                    // Retrieve the recipe data for each ID and update the recipeHistory list.
                    for (String recipeId : recipeIds) {
                        getRecipeInfo(recipeId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors in reading from the database
                }
            });
        } else {
            // Handle the case when the user is not logged in.
        }
    }

    @Override
    public void onRecipeClick(RecipeSummary recipe) {
        // Handle the click event when a recipe card is clicked
        Intent intent = new Intent(RecipeHistory.this, DisplayRecipeSelected.class);
        intent.putExtra("id", recipe.getId());
        startActivity(intent);
    }

    private class FetchRecipeTask extends AsyncTask<String, Void, RecipeSummary> {
        @Override
        protected RecipeSummary doInBackground(String... params) {
            // This method performs a network request in the background
            String recipeId = params[0];
            RecipeSummary recipeData = new RecipeSummary();

            try {
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
            recipeHistory.add(recipeData);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void getRecipeInfo(String recipeId) {
        // Start the FetchRecipeTask to fetch recipe data
        new FetchRecipeTask().execute(recipeId);
    }
}
