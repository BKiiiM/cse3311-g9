package com.example.recipegeneratorapplication;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    Context context;
    List<RecipeSummary> recipes;
    private OnRecipeClickListener onRecipeClickListener;

    public RecipeAdapter(Context context, List<RecipeSummary> recipes, OnRecipeClickListener listener) {
        this.context = context;
        this.recipes = recipes;
        this.onRecipeClickListener = listener;
    }
    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeSummary recipe);
    }
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeSummary recipe = recipes.get(position);

        holder.nameView.setText(recipes.get(position).getTitle());
        Picasso.get()
                .load(recipes.get(position).getRecipeImageURL()) // Provide the image URL
                .into(holder.imageView); // Specify the target ImageView



        holder.itemView.setOnClickListener(view -> {
            if (onRecipeClickListener != null) {
                onRecipeClickListener.onRecipeClick(recipes.get(position)); // Call the click listener
            }
        });
    }

    @Override
    public int getItemCount() {
        if (recipes != null) {
            return recipes.size();
        } else {
            return 0;
        }
    }


}
