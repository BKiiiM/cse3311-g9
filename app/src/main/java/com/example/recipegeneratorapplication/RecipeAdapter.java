package com.example.recipegeneratorapplication;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    Context context;
    List<RecipeSummary> recipes;

    public RecipeAdapter(Context context, List<RecipeSummary> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.nameView.setText(recipes.get(position).getTitle());
        Picasso.get()
                .load(recipes.get(position).getRecipeImageURL()) // Provide the image URL
                .into(holder.imageView); // Specify the target ImageView
    }

    @Override
    public int getItemCount() {
        if (recipes != null) {
            return recipes.size();
        } else {
            return 0;
        }    }
}
