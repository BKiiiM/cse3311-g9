package com.example.recipegeneratorapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class SearchByIngredientsActivity extends AppCompatActivity
{
    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;
    AutoCompleteTextView ingredient1;
    AutoCompleteTextView ingredient2;
    AutoCompleteTextView ingredient3;
    Button searchByIngredientButton;
    ListView resultListByIngredients;
    ArrayAdapter<RecipeSummary> adapterView;
    ArrayList<RecipeSummary> listByIngredients = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_ingredients);

        //create references to ingredient 1,2,3 inputs, search button and list view
        ingredient1 =findViewById(R.id.ingredient_1_input);
        ingredient2 =findViewById(R.id.ingredient_2_input);
        ingredient3 =findViewById(R.id.ingredient_3_input);
        searchByIngredientButton =findViewById(R.id.search_ingredients);
        resultListByIngredients = findViewById(R.id.result_search_ingredients_ListView);

        adapterView = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listByIngredients);
        resultListByIngredients.setAdapter(adapterView);
        resultListByIngredients.setOnItemClickListener(this::onItemClick);

        //setup an OnClickListener to call the function onButtonClick when the
        //search button is clicked
        searchByIngredientButton.setOnClickListener(this::onButtonClick);

    }
    private void onButtonClick(View view)
    {
        String query1 = ingredient1.getText().toString().trim();
        String query2 = ingredient2.getText().toString().trim();
        String query3 = ingredient3.getText().toString().trim();

        String all_ingredients_query = query1 + ","+ query2 + "," + query3;

        if (!query1.isEmpty() || !query2.isEmpty() || !query3.isEmpty())
        {
            new recipeSearchByIngredients().execute(all_ingredients_query);
        }
    }
    //we need to know the index of the recipe selected by the user
    //to extract information from the recipe
    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        RecipeSummary recipeData = listByIngredients.get(i);
        int recipeId = recipeData.getId();
        Intent myIntent = new Intent(SearchByIngredientsActivity.this, DisplayRecipeSelected.class);
        myIntent.putExtra("id", recipeId);
        SearchByIngredientsActivity.this.startActivity(myIntent);

    }
    //takes a String with the query containing the ingredients(ingredient1,ingredient2,ingredient3)
    //has a result of matching recipes with their id and title, these will be display in the ListView
    //widget on the app screen
    private class recipeSearchByIngredients extends AsyncTask<String, Void, ArrayList<RecipeSummary>>
    {
        @Override
        protected ArrayList<RecipeSummary> doInBackground(String... params)
        {
            String ingredientInput = params[0];
            ArrayList<RecipeSummary> listResults = new ArrayList<>();
            try
            {

                //creates string with entire url to search using HttpURLConnection
                String spoonacularUrl = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=" + API_KEY +
                        "&ingredients=" + ingredientInput+"&number=30";

                URL url = new URL(spoonacularUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                //asks for response from url
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
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
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return listResults;


        }
        @Override
        protected void onPostExecute(ArrayList<RecipeSummary> result)
        {
            super.onPostExecute(result);
            listByIngredients.clear();
            listByIngredients.addAll(result);
            adapterView.notifyDataSetChanged();
        }
    }
}











