package com.example.recipe.data;

import android.content.Context;

import com.example.recipe.utility.AppPreference;
import com.example.recipe.utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by rajnish on 19/10/15.
 */
public class UserInfo {

    public static final String USER_INFO_PREFIX = "USER_INFO_PREFIX_";
    private static UserInfo sInstance;
    Context mContext;

    private UserInfo(Context context) {
        mContext = context;
    }

    public static UserInfo getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UserInfo(context);
        }
        return sInstance;
    }

    public void logRecipeViewedInfo(RecipeInfo info) {
        String[] recipeViewedTags = Utility.getCategories(info);
        if (recipeViewedTags == null) {
            return;
        }

        for (String tag : recipeViewedTags) {
            String key = USER_INFO_PREFIX + tag;
            int value = AppPreference.getInstance(mContext).getInteger(key, 0);
            AppPreference.getInstance(mContext).putInteger(key, ++value);
        }
    }

    public TreeMap fetchDataForFeed() {
        ArrayList<String> uniqueTags = RecipeDataStore.getsInstance(mContext).getUniqueTags();
        HashMap<String, Integer> userViewBasedTags = new HashMap<>();
        Utility.ValueComparator bvc = new Utility.ValueComparator(userViewBasedTags);
        for (String tag : uniqueTags) {
            String key = USER_INFO_PREFIX + tag;
            int value = AppPreference.getInstance(mContext).getInteger(key, 0);
            userViewBasedTags.put(tag, value);
        }

        TreeMap sortedMap = new TreeMap(bvc);
        sortedMap.putAll(userViewBasedTags);
        return sortedMap;
    }
}
