package com.example.recipe.data;

import android.net.Uri;

import com.google.gson.Gson;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 13/9/15.
 */
public class RecipeInfo implements Serializable{
    String objectId;
    int recipeinfoId;
    public String title;
    String description;
    public Uri imageUrl;
    public List<String> ingredients;
    public List<String> directions;
    public String serves;
    public String preparationTime;
    public List<String> nutritionList;
    List<String> mTags;
    String category;

    public static RecipeInfo getRecipeDescription(String json) {
        Gson gson = new Gson();
        RecipeInfo desc = gson.fromJson(json, RecipeInfo.class);
        return desc;
    }

    public int getRecipeinfoId() {
        return recipeinfoId;
    }

    public void setRecipeinfoId(int recipeinfoId) {
        this.recipeinfoId = recipeinfoId;
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



    public String getmServing() {
        return serves;
    }


    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String mCategory) {
        this.category = mCategory;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> mTags) {
        this.mTags = mTags;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String mDescription) {
        this.description = mDescription;
    }

    public static RecipeInfo getRecipeInfo(ParseObject parseObject) {
        RecipeInfo recipeInfo = new RecipeInfo();
        recipeInfo.setObjectId(parseObject.getObjectId());
        recipeInfo.recipeinfoId = parseObject.getInt("recipeinfo_id");
        recipeInfo.setTitle(parseObject.getString("title"));
        recipeInfo.setDescription(parseObject.getString("description"));
        recipeInfo.setPreparationTime(parseObject.getString("cooking_time"));
        recipeInfo.setCategory(parseObject.getString("category"));
//        recipeInfo.setImageUrl(Config.sRecipeInfoBaseUrl + "/" + recipeInfo.recipeinfo_id + ".jpg");
        return recipeInfo;
    }
}
