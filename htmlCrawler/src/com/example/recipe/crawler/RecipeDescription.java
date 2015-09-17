package com.example.recipe.crawler;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 13/9/15.
 */
public class RecipeDescription implements Serializable{


    public String title;
    public String description;
    public String imageUrl;
    public List<String> ingredients;
    public List<String> directions;
    public List<String> nutritionList;
    public String serves;
    public String preparationTime;
  

//    public static RecipeDescription getRecipeDescription() {
//        String json = Util.jsonData;
//        Gson gson = new Gson();
//        RecipeDescription desc = gson.fromJson(json, RecipeDescription.class);
//        return desc;
//    }

    public String getTitle() {
        return title;
    }



    public String getImageUrl() {
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

}
