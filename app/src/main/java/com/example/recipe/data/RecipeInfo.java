package com.example.recipe.data;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 5/9/15.
 */
public class RecipeInfo {
    String objectId;
    int recipeinfoId;
    ArrayList<String> mImageUrl;
    String mTitle;
    String mDescription;
    List<String> mIngredients;
    String mCookingTime;
    String mServing;
    List<String> mTags;
    String mCategory;

    public int getRecipeinfoId() {
        return recipeinfoId;
    }

    public void setRecipeinfoId(int recipeinfoId) {
        this.recipeinfoId = recipeinfoId;
    }

    public ArrayList<String> getImageUrlList() {
        return mImageUrl;
    }

    public void setImageUrl(ArrayList<String>  mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public List<String> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<String> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public String getCookingTime() {
        return mCookingTime;
    }

    public void setCookingTime(String mCookingTime) {
        this.mCookingTime = mCookingTime;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
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

    public static RecipeInfo getRecipeInfo(ParseObject parseObject) {
        RecipeInfo recipeInfo = new RecipeInfo();
        recipeInfo.setObjectId(parseObject.getObjectId());
        recipeInfo.recipeinfoId = parseObject.getInt("recipeinfo_id");
        recipeInfo.setTitle(parseObject.getString("title"));
        recipeInfo.setDescription(parseObject.getString("description"));
        recipeInfo.setCookingTime(parseObject.getString("cooking_time"));
        recipeInfo.setCategory(parseObject.getString("category"));
//        recipeInfo.setImageUrl(Config.sRecipeInfoBaseUrl + "/" + recipeInfo.recipeinfo_id + ".jpg");
        return recipeInfo;
    }

    public static void updateRecipeInfo(RecipeInfo info, final HashMap<String, String> list) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RecipeInfo.class.getSimpleName());
        query.getInBackground(info.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    for (String key : list.keySet()) {
                        gameScore.put(key, list.get(key));
                    }
                    gameScore.saveInBackground();
                }
            }
        });
    }
}
