package com.example.recipegeneratorapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

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
    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;
    AutoCompleteTextView inputRecipeName;
    AutoCompleteTextView cuisine;
    Button searchButton;
    CheckBox ExcludeGluten;
    CheckBox ExcludeDairy;
    CheckBox ExcludePeanut;
    TextView RecipeNameMessage;
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
        cuisine = findViewById(R.id.cuisine_input);
        //cuisine = thisView.getResources().getStringArray(R.array.cuisines);
        ExcludeGluten = findViewById(R.id.gluten_checkBox);
        ExcludeDairy = findViewById(R.id.dairy_checkBox);
        ExcludePeanut = findViewById(R.id.peanut_checkbox);

        //changed message title to test the api call
        RecipeNameMessage =findViewById(R.id.enter_recipe_name_TextView);

        //We need an array adapter to convert the ArrayList of objects (recipeList) into View items
        // that will be loaded into the ListView container.
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        resultListByName.setAdapter(adapter);
        resultListByName.setOnItemClickListener(this::onItemClick);



        searchButton.setOnClickListener(v ->
        {
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

    private class recipeSearchByName extends AsyncTask<String, Void, ArrayList<RecipeSummary>>
    {
        @Override
        protected ArrayList<RecipeSummary> doInBackground(String... params)
        {

            String intolerances = "";
            if (ExcludeGluten.isChecked())
            {
                intolerances = "gluten";
            }
            if(ExcludeDairy.isChecked())
            {
                intolerances = intolerances+",dairy";
            }
            if(ExcludePeanut.isChecked())
            {
                intolerances = intolerances+",peanut";
            }




            String query = params[0];
            ArrayList<RecipeSummary> result = new ArrayList<>();

            try {
                //creates string with entire url to search using HttpURLConnection
                String spoonacularUrl = "https://api.spoonacular.com/recipes/complexSearch" +
                        "?apiKey=" + API_KEY +
                        "&query=" + query +
                        "&intolerances=" + intolerances +
                        "&cuisine=" + cuisine +
                        "&number=30"; //number of results shown, we can change this number

                //THIS PRINTOUT TESTS OUR API CALL
               /*String test =spoonacularUrl;
                       Test.setText(test);*/

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

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray results = jsonObject.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++)
                    {
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
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<RecipeSummary> result)
        {
            super.onPostExecute(result);
            recipeList.clear();
            recipeList.addAll(result);
            adapter.notifyDataSetChanged();
        }
    }
}


