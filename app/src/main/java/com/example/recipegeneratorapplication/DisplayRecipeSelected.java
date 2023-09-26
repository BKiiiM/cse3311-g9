package com.example.recipegeneratorapplication;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DisplayRecipeSelected extends AppCompatActivity
{
    TextView selectedRecipeId;
    TextView recipeTitle;
    ImageView recipePhoto;
    private static final String API_KEY = "7bc4cc04deb34da4b63e486dd734ca93";
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }
    private class recipeSearchById extends AsyncTask<Integer, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer... params) {
            int id = params[0];
            JSONObject jsonObject = null;

            try {
                //creates string with entire url to search in the browser
                String spoonacularUrl = "https://api.spoonacular.com/recipes/" +id+"/information"+
                        "?apiKey=" + API_KEY;

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

                    jsonObject = new JSONObject(response.toString());

                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            //updating UI with data from results of JSONObject all fields from API are set here

            try {
                recipeTitle.setText(result.getString("title"));
                //recipePhoto.setImageBitmap(getBitmapFromURL(result.getString("image")));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe_selected);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);
        selectedRecipeId = findViewById(R.id.recipe_API_id);
        recipeTitle = findViewById(R.id.recipeTitle);
        recipePhoto = findViewById(R.id.recipe_photo);
        //displays the recipe id on screen
        selectedRecipeId.setText(id+"");
        new recipeSearchById().execute(id);
    }


}