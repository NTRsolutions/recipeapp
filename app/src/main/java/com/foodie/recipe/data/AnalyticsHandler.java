package com.foodie.recipe.data;

import android.content.Context;
import android.util.Log;

import com.foodie.recipe.ApplicationImpl;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by rajnish on 1/11/15.
 */
public class AnalyticsHandler {
    public static final String TAG = AnalyticsHandler.class.getSimpleName();
    private static AnalyticsHandler sInstance;
    private Tracker mTracker;

    private AnalyticsHandler(Context context) {

        ApplicationImpl application =  (ApplicationImpl)(context);
        mTracker = application.getDefaultTracker();
    }

    public synchronized  static AnalyticsHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AnalyticsHandler(context.getApplicationContext());
        }

        return sInstance;
    }

    public void sendScreenName(String name) {
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
