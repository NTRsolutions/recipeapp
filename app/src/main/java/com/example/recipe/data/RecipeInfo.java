package com.example.recipe.data;

import java.util.List;

/**
 * Created by root on 5/9/15.
 */
public class RecipeInfo {
    int id;
    String mImageUrl;
    String mTitle;
    String mDescription;
    List<String> mIngredients;
    int mCookingTime;
    String mCategory;
    List<String> mTags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
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

    public int getCookingTime() {
        return mCookingTime;
    }

    public void setCookingTime(int mCookingTime) {
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

    public void setmTags(List<String> mTags) {
        this.mTags = mTags;
    }
}
