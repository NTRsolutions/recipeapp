package com.example.recipe.data;

import android.util.Log;

/**
 * Created by rajnish on 13/10/15.
 */
public class TextToSpeechDesc {
    public static final String TAG = TextToSpeechDesc.class.getSimpleName();

    public String convertTextToSpeechDescription(RecipeInfo recipeInfo){
        String convertedString = null;

        convertedString = "The name of the recipe is " + recipeInfo.getTitle() + "\n"
                + recipeInfo.getmServing() + "\n and is " +
                recipeInfo.getPreparationTime() + "\n The ingredients required for this recipe are \n "
                + recipeInfo.getIngredients() + " \n Directions to prepare is as follows \n"
                + recipeInfo.getDirections();

        Log.d(TAG, convertedString);
        return convertedString;

    }
}
