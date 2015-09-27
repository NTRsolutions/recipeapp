package com.example.recipe.data;

import android.net.Uri;

import com.example.recipe.utility.Config;
import com.google.gson.Gson;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 13/9/15.
 */
public class RecipeInfo implements Serializable{
    String objectId;
    int recipeinfoId;
    public String title;
    String description;
    public ArrayList<String> imageUrlList;
    public List<String> ingredients;
    public List<String> directions;
    public String serves;
    public String preparationTime;
    public List<String> nutritionList;
    List<String> mTags;
    String category;
    Long lastViewedTime;

    // dynamically generated, dont serialise
    public transient Uri imageUrl;

    public static RecipeInfo getRecipeDescription(String json) {
        Gson gson = new Gson();
        RecipeInfo desc = gson.fromJson(json, RecipeInfo.class);
        return desc;
    }

    public String getDocId() {
        return String.valueOf(recipeinfoId);
    }

    // will be used as docId
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

    public void setImageUrlList(ArrayList<String> list) {
        this.imageUrlList = list;
    }

    public ArrayList<String> getImageUrlList() {
        return imageUrlList;
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


    public Long getLastViewedTime() {
        return lastViewedTime;
    }

    public void setLastViewedTime(Long lastViewedTime) {
        this.lastViewedTime = lastViewedTime;
    }

    public void updateLastViewedTime() {
        this.lastViewedTime = System.currentTimeMillis();
        RecipeDataStore.getsInstance(Config.APPLICATION_CONTEXT).updateDoc(this);
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

    public static void updateRecipeInfoCategoryOnCloud(RecipeInfo info, final HashMap<String, String> list) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RecipeInfo.class.getSimpleName());
        query.getInBackground(info.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject obj, ParseException e) {
                if (e == null) {
                    for (String key : list.keySet()) {
                        obj.put(key, list.get(key));
                    }
                    obj.saveInBackground();
                }
            }
        });
    }
}
