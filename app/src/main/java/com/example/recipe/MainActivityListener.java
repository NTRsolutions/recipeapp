package com.example.recipe;

import com.example.recipe.data.CategoryDataStore;
import com.example.recipe.data.RecipeDescription;

import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public interface MainActivityListener {
   void showDetailView(RecipeDescription recipeDescription);
}
