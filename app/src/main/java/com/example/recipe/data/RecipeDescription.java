package com.example.recipe.data;

import com.example.recipe.utility.Util;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by root on 13/9/15.
 */
public class RecipeDescription {
    public String title;
    public String imageUrl;
    public List<String> ingredients;
    public List<String> directions;
    public String serves;
    public String preparationTime;

    public static RecipeDescription getRecipeDescription() {
        String json = Util.jsonData;
        Gson gson = new Gson();
        RecipeDescription desc = gson.fromJson(json, RecipeDescription.class);
        return desc;
    }
}
