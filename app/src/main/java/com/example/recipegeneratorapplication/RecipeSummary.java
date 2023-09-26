package com.example.recipegeneratorapplication;
//created class RecipeSummary that will contain all the fields we need
// from Spoonacular recipes to generated the searched and selected recipe

public class RecipeSummary
{
    int id;
    String title;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString()
    {
        return title;
    }

}
