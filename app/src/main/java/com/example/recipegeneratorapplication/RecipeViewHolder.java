package com.example.recipegeneratorapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeViewHolder extends RecyclerView.ViewHolder {

    // Declare member variables for views
    ImageView imageView;
    TextView nameView;

    // Constructor for RecipeViewHolder, called when a new ViewHolder is created
    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize the ImageView and TextView by finding them in the provided View
        imageView = itemView.findViewById(R.id.recipeImage);
        nameView = itemView.findViewById(R.id.recipeName);
    }
}
