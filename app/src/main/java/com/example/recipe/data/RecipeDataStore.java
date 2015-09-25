package com.example.recipe.data;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeDataStore {
    public static final String TAG = "RecipeDataStore";
    static List<RecipeInfo> sRecipeInfoList;

    public interface RecipeDataStoreListener {
       void onDataFetchComplete(List<RecipeInfo> list);
    }

    public static void fetchAllInfoData(final RecipeDataStoreListener listener) {

        if (sRecipeInfoList == null) {
            sRecipeInfoList = new ArrayList<>();
        }

        if (listener != null && sRecipeInfoList.size() >0) {
            listener.onDataFetchComplete(sRecipeInfoList);
            return;
        }

        ParseQuery<ParseObject> category = ParseQuery.getQuery(RecipeInfo.class.getSimpleName());
        category.setLimit(1000);
        category.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                List<RecipeInfo> list = new ArrayList<>();
                for (ParseObject object : results) {
                    RecipeInfo category = RecipeInfo.getRecipeInfo(object);
                    Log.d(TAG, "got result ");
                    list.add(category);
                }
                if (listener != null) {
                    sRecipeInfoList = list;
                    listener.onDataFetchComplete(list);
                }
            }
        });
    }
}
