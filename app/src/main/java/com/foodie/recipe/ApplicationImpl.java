package com.foodie.recipe;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.foodie.recipe.data.ParseDataFetcherService;
import com.foodie.recipe.utility.Config;
import com.foodie.recipe.utility.Utility;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import io.fabric.sdk.android.Fabric;


public class ApplicationImpl extends Application {
    public static final String TAG = ApplicationImpl.class.getSimpleName();
    private MyActivityLifecycleCallbacks mCallbacks;
    private Tracker mTracker;
    private static final String PROPERTY_ID = "UA-40303534-2";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);

        if (BuildConfig.DEBUG) {
            // Development Config's
            Parse.initialize(this, "ImCRjnmaKzEl3NXutGNMPc808eYXUbsbH6E1rvN1",
                    "0RwNgszMvs5bCnvkgRi3gXrbXAhVwUvUtrtCfQez");
        } else {
            // Production Configs
            Parse.initialize(this, "BjPG9N2ZepJL2at4Y8267mB5h593H5A89Ianq1T0",
                    "o1C6ZL7kUZkGF0zjuztg9Qz75sSfxx7eWiHjnCRv");
        }

        Config.initialize(this.getApplicationContext());

        mCallbacks = new MyActivityLifecycleCallbacks(this);
        registerActivityLifecycleCallbacks(mCallbacks);

    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            if (BuildConfig.DEBUG) {
                analytics.setDryRun(true);
            }

            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(PROPERTY_ID);
        }
        return mTracker;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(mCallbacks);
    }

    public static class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        private int activityStateCounter;
        private Context mContext;
        long mLastAppStoppedTime;

        public MyActivityLifecycleCallbacks(Context context) {
            mContext = context;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(TAG, activity.getClass().getSimpleName() + " onCreate(Bundle)");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i(TAG, activity.getClass().getSimpleName() + " onStart()");
            Log.i(TAG, "onStart activityStateCounter : " + activityStateCounter);
            // Application was in background.
            if (activityStateCounter == 0) {
                Utility.getInstance(mContext).init();
            }
            activityStateCounter++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.i(TAG, activity.getClass().getSimpleName() + " onResume()");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.i(TAG, activity.getClass().getSimpleName() + "onPause()");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.i(TAG, activity.getClass().getSimpleName() + "onSaveInstanceState(Bundle)");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i(TAG, activity.getClass().getSimpleName() + "onStop()");
            Log.i(TAG, "Stop activityStateCounter : " + activityStateCounter);
            mLastAppStoppedTime = System.currentTimeMillis();
            activityStateCounter--;
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, activity.getClass().getSimpleName() + "onDestroy()");
            Log.i(TAG, "Destroyed activityStateCounter : " + activityStateCounter);
            if (activityStateCounter == 0) {
                Intent backgroundDataFetcherIntent = new Intent(
                        mContext, ParseDataFetcherService.class);
                mContext.startService(backgroundDataFetcherIntent);

                Utility.getInstance(mContext).onDestroy();
            }
        }
    }

}
