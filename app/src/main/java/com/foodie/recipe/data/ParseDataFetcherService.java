package com.foodie.recipe.data;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.foodie.recipe.data.RecipeDataStore.RecipeDataStoreListener;
import com.foodie.recipe.utility.AppPreference;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 1/11/15.
 */
public class ParseDataFetcherService extends IntentService {
    public static final int sSingleBatchLimit = 1000;
    public static final String LAST_DATA_FETCHED_UPTO_KEY = "LAST_DATA_FETCHED_UPTO_KEY";

    public ParseDataFetcherService() {
        super("ParseDataFetcherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startIncrementalDataFetch();
    }

    void startIncrementalDataFetch() {
        int resultCount = fetchAllInfoData();
        while (resultCount > 0) {
            resultCount = fetchAllInfoData();
        }
    }

    public int fetchAllInfoData() {
        int lastDataFetchedUpto = AppPreference.getInstance(this).getInteger(LAST_DATA_FETCHED_UPTO_KEY, 0);
        List<ParseObject> results = new ArrayList<>();
        ParseQuery<ParseObject> category = ParseQuery.getQuery(RecipeInfo.class.getSimpleName());
        try {
            category.setLimit(sSingleBatchLimit);
            category.orderByAscending("added_at");
            category.whereGreaterThan("added_at", lastDataFetchedUpto);
            results = category.find();
            for (ParseObject object : results) {
                RecipeInfo info = RecipeInfo.getRecipeInfo(object);
                RecipeDataStore.getsInstance(ParseDataFetcherService.this).updateDoc(info);
                AppPreference.getInstance(this).putInteger(LAST_DATA_FETCHED_UPTO_KEY, (int)info.getAddedAt());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return results.size();
    }
}
