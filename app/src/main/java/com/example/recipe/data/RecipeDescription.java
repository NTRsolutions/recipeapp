package com.example.recipe.data;

import android.net.Uri;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 13/9/15.
 */
public class RecipeDescription implements Serializable{
    public String title;
    public Uri imageUrl;
    public List<String> ingredients;
    public List<String> directions;
    public String serves;
    public String preparationTime;
    public List<String> nutritionList;

    public static RecipeDescription getRecipeDescription(String json) {
        Gson gson = new Gson();
        RecipeDescription desc = gson.fromJson(json, RecipeDescription.class);
        return desc;
    }

    public String getTitle() {
        return title;
    }



    public Uri getImageUrl() {
        return imageUrl;
    }



    public List<String> getIngredients() {
        return ingredients;
    }



    public List<String> getDirections() {
        return directions;
    }



    public String getServes() {
        return serves;
    }


    public String getPreparationTime() {
        return preparationTime;
    }

    public List<String> getNutritionList() {
        return nutritionList;
    }

    public void setNutritionList(List<String> nutritionList) {
        this.nutritionList = nutritionList;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }
}
