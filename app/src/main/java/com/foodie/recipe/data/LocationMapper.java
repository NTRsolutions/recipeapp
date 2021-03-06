package com.foodie.recipe.data;

import android.content.Context;
import android.util.Log;

import com.foodie.recipe.utility.AppPreference;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by rajnish on 31/10/15.
 */

public class LocationMapper {
    public static final String TAG = LocationMapper.class.getSimpleName();
    public static final String DEFAULT_KEY = "default";

    public interface LocationMapperUpdate {
        void onLocationMapperComplete();
    }

    private Context mContext;
    private static LocationMapper sInstance;

    private LocationMapper(Context context) {
        mContext = context;
    };

    public static LocationMapper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocationMapper(context);
        }

        return sInstance;
    }

    public static final String LOCATION_NAME_KEY = "location_name";
    public static final String META_DATA_KEY = "meta_data";
    HashMap<String, String> mMapper = new HashMap<>();

    public void fetchLocationMapperData(final LocationMapperUpdate listener) {
        ParseQuery<ParseObject> category = ParseQuery.getQuery(LocationMapper.class.getSimpleName());
        category.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException exception) {
                if (exception != null  || results == null) {
                    Log.d(TAG, exception.getCause().toString());
                    return;
                }
                // Boiler Plate Code
                try {
                    fillLocationMapper(results);
                    boostPromotedTags(mContext);
                } catch (Exception ex) {
                }

                if (listener != null) {
                    listener.onLocationMapperComplete();
                }
            }
        });
    }

    void fillLocationMapper (List<ParseObject> result) {
        for(ParseObject object : result) {
            String key = object.getString(LOCATION_NAME_KEY).toLowerCase();
            String value = object.getString(META_DATA_KEY).toLowerCase();
            mMapper.put(key, value);
        }
    }

    public void boostPromotedTags(Context context) {
        boolean isReturningUser = AppPreference.getInstance(context).getBoolean(
                UserInfo.IS_RETURNING_USER_KEY, false);

        // boost only for new User
        if(isReturningUser) {
          return;
        }
        
        // southindian:100,bengali:90
        String metaData = getMetaData();
        if (metaData == null || metaData.equalsIgnoreCase("")) {
            return;
        }

        String[] tagProbabilityList = metaData.split(",");

        TreeMap<Integer, String > treeMap = new TreeMap<>();
        int totalCount = 0;
        for (String tagProbability : tagProbabilityList) {
            String tag = tagProbability.split(":")[0];
            int probability = Integer.parseInt(tagProbability.split(":")[1]);
            treeMap.put(probability, tag);
            totalCount += probability;
        }

        int maxBoost = 10;
        for (int treeKey : treeMap.keySet()) {
            String tag = treeMap.get(treeKey);
            String prefKey = UserInfo.USER_INFO_PREFIX + tag;
            int normalizedBoost = Math.round((treeKey * maxBoost / totalCount) + 1);
            int value = AppPreference.getInstance(context).getInteger(prefKey, 0);
            AppPreference.getInstance(context).putInteger(prefKey, value + normalizedBoost);
        }
    }

    String getMetaData() {
        if (mMapper == null || mMapper.size() == 0) {
            return null;
        }

        String metaData = mMapper.get(DEFAULT_KEY);
        String currentLocation = AppPreference.getInstance(mContext).getString(
                UserInfo.GEO_ADMIN_AREA, "");

        if (currentLocation == null || currentLocation.equalsIgnoreCase("")) {
            return metaData;
        }

        String location = currentLocation.toLowerCase();
        if (mMapper.get(location) == null) {
            return metaData;
        }

        return mMapper.get(location);
    }
}
