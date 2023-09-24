package com.example.recipegeneratorapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SearchByNameActivity extends AppCompatActivity
{
    EditText inputRecipeName;

    ListView resultList;

    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);

        //create a reference to input text box for recipe name

        inputRecipeName = findViewById(R.id.recipe_name_input_box);

        resultList = findViewById(R.id.result_recipe_list);

        button = findViewById(R.id.search_name_button);

        //when search button is clicked the method search_recipes_by_name is called
        //use this if onClick is not used and we want to set our buttons dynamically,
        //both methods are equivalent

        button.setOnClickListener(this::searchRecipesByName);

    }

    public void searchRecipesByName(View v)
    {

        Log.d("message", "Have a great day!");

        String arrList[] = {"lasagna","hamburger","casserole"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrList);

        resultList.setAdapter(adapter);


    }

    //
}