package com.example.recipe.data;

import android.util.Log;

/**
 * Created by rajnish on 13/10/15.
 */
public class TextToSpeechDesc {
    public static final String TAG = TextToSpeechDesc.class.getSimpleName();

    public String convertTextToSpeechDescription(RecipeInfo recipeInfo){
        String convertedString = null;

        convertedString = "The name of the recipe is " + recipeInfo.getTitle()
                + recipeInfo.getmServing() + "and is " +
                recipeInfo.getPreparationTime() + "The ingredients required for this recipe are "
                + recipeInfo.getIngredients() + "Directions to prepare is as follows "
                + recipeInfo.getDirections();

        Log.d(TAG, convertedString);
        return convertedString;

    }
}
