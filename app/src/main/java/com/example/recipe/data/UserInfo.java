package com.example.recipe.data;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.example.recipe.utility.AppPreference;
import com.example.recipe.utility.Utility;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by rajnish on 19/10/15.
 */

public class UserInfo {
    public static final String TAG = UserInfo.class.getSimpleName();
    public static final String USER_INFO_PREFIX = "USER_INFO_PREFIX_";
    public static final String GEO_LOCATION = "GEO_LOCATION";
    public static final String GEO_LATITUDE = "GEO_LATITUDE";
    public static final String GEO_LONGITUDE = "GEO_LONGITUDE";

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

        updateRecipeInfoViewCount(info);
    }


    private void updateRecipeInfoViewCount(final RecipeInfo info) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RecipeInfoStats.class.getSimpleName());
        query.whereEqualTo(RecipeInfoStats.Keys.RECIPEINFO_ID.getValue(), info.getRecipeinfoId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
               public void done(List<ParseObject> results, ParseException e) {
                   if (results == null || e != null) {
                       return;
                   }

                   if (results != null && results.size() == 0) {
                       ParseObject gameScore = new ParseObject(
                               RecipeInfoStats.class.getSimpleName());
                       gameScore.put(RecipeInfoStats.Keys.RECIPEINFO_ID.getValue()
                               , info.getRecipeinfoId());
                       gameScore.put(RecipeInfoStats.Keys.TITLE.getValue(), info.getTitle());
                       gameScore.put(RecipeInfoStats.Keys.FAVOURATE_COUNT.getValue(), 0);
                       gameScore.put(RecipeInfoStats.Keys.VIEW_COUNT.getValue(), 1);
                       gameScore.saveInBackground();
                       Log.d(TAG, "zero Result , Creating new Object");
                       return;
                   }

                   ParseObject recipeInfoStats = results.get(0);
                   recipeInfoStats.increment(RecipeInfoStats.Keys.VIEW_COUNT.getValue());
                   recipeInfoStats.saveInBackground();

               }
           }
        );
    }

    public void updateRecipeInfoFavorateCount(final RecipeInfo info, final boolean increment) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RecipeInfoStats.class.getSimpleName());
        query.whereEqualTo(RecipeInfoStats.Keys.RECIPEINFO_ID.getValue(), info.getRecipeinfoId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
               public void done(List<ParseObject> results, ParseException e) {
                   if (results == null || e != null) {
                       return;
                   }

                   if (results != null && results.size() == 0 ) {
                       ParseObject gameScore = new ParseObject(RecipeInfoStats.class.getSimpleName());
                       gameScore.put(RecipeInfoStats.Keys.RECIPEINFO_ID.getValue(), info.getRecipeinfoId());
                       gameScore.put(RecipeInfoStats.Keys.TITLE.getValue(), info.getTitle());
                       gameScore.put(RecipeInfoStats.Keys.FAVOURATE_COUNT.getValue(), 1);
                       gameScore.put(RecipeInfoStats.Keys.VIEW_COUNT.getValue(), 1);
                       gameScore.saveInBackground();
                       Log.d(TAG, "zero Result , Creating new Object");
                       return;
                   }


                   ParseObject recipeInfoStats = results.get(0);
                   if (increment) {
                       recipeInfoStats .increment(RecipeInfoStats.Keys.FAVOURATE_COUNT.getValue());
                   } else {
                       recipeInfoStats .increment(RecipeInfoStats.Keys.FAVOURATE_COUNT.getValue(), -1);
                   }
                   recipeInfoStats .saveInBackground();
               }
           }
        );
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

    public boolean GetUserLocation(){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                Log.d(TAG, location.getLatitude() + " : " + location.getLongitude());
                Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ;
                }

                if (addresses.size() > 0) {
                    Log.d(TAG, addresses.get(0).getLocality());
                }

                AppPreference.getInstance(mContext).putString(GEO_LOCATION, addresses.get(0).getLocality());
                AppPreference.getInstance(mContext).putString(GEO_LATITUDE, String.valueOf(location.getLatitude()));
                AppPreference.getInstance(mContext).putString(GEO_LONGITUDE, String.valueOf(location.getLatitude()));


            }
        };

        MyLocation myLocation = new MyLocation();
        boolean foundLocation = myLocation.getLocation(mContext, locationResult);


        return foundLocation;
    }
}
