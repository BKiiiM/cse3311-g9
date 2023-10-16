package com.example.recipegeneratorapplication;
//created class RecipeSummary that will contain all the fields we need
// from Spoonacular to generate the searched and selected recipe

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

    //creates toString method to return the string with the title to display on the ListView widget
    //this method overrides the default toString method from java because returns
    //the name of the class and memory address instead of the title string
    public String toString(){ return title; }

}
