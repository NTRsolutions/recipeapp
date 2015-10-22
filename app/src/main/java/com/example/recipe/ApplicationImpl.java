package com.example.recipe;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.recipe.utility.Config;
import com.example.recipe.utility.Utility;
import com.parse.Parse;
import com.parse.ParseCrashReporting;

/**
 * Created by saurabh on 07/08/15.
 */
public class ApplicationImpl extends Application {
    public static final String TAG = ApplicationImpl.class.getSimpleName();
    private MyActivityLifecycleCallbacks mCallbacks;

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, "BjPG9N2ZepJL2at4Y8267mB5h593H5A89Ianq1T0", "o1C6ZL7kUZkGF0zjuztg9Qz75sSfxx7eWiHjnCRv");

        Config.initialize(this.getApplicationContext());

        mCallbacks = new MyActivityLifecycleCallbacks(this);
        registerActivityLifecycleCallbacks(mCallbacks);

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
            Log.i(TAG, "activityStateCounter : " + activityStateCounter);
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
            mLastAppStoppedTime = System.currentTimeMillis();
            activityStateCounter--;
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, activity.getClass().getSimpleName() + "onDestroy()");

            if (activityStateCounter == 0) {
                Utility.getInstance(mContext).onDestroy();
            }
        }
    }

}
