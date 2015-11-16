package com.foodie.recipe;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.foodie.recipe.data.LocationMapper;
import com.foodie.recipe.data.LocationMapper.LocationMapperUpdate;
import com.foodie.recipe.data.RecipeDataStore;
import com.foodie.recipe.data.RecipeDataStore.RecipeDataStoreListener;
import com.foodie.recipe.data.RecipeInfo;
import com.foodie.recipe.data.UserInfo;
import com.foodie.recipe.utility.AppPreference;
import com.foodie.recipe.utility.Utility;

import java.lang.ref.WeakReference;
import java.util.List;


public class SplashScreenActivity extends AppCompatActivity {
    public static final String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Gets Geo Location
        if (Utility.isNetworkAvailable(this)) {
            UserInfo.getInstance(this).getUserLocation();
        }

        Utility.getInstance(this).init();
        SplashAsyncTask task = new SplashAsyncTask(this);
        task.execute("SplashAsyncTask");

        RecipeDataStore.getsInstance(this).checkAndUnZipJsonData();
    }

    void onDataFetchComplete() {
        AppPreference.getInstance(this).putBoolean(UserInfo.IS_RETURNING_USER_KEY, true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
     public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecipeDataStore.getsInstance(this).dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

    private class SplashAsyncTask extends AsyncTask<String , Integer, Boolean> {

        WeakReference<SplashScreenActivity> activityWeakReference;
        Context mContext;
        boolean dataFetchComplete;
        boolean locationMapperComplete;

        SplashAsyncTask(SplashScreenActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
            mContext = activity.getApplicationContext();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean isReturningUser = AppPreference.getInstance(mContext).getBoolean(
                    UserInfo.IS_RETURNING_USER_KEY, false);

            if (isReturningUser) {
                dataFetchComplete = true;
            } else {
                //TODO to remove and implement sequential download's
                RecipeDataStore.getsInstance(mContext).fetchAllInfoData(
                        new RecipeDataStoreListenerImpl(this), 500);
            }

            LocationMapper.getInstance(mContext).fetchLocationMapperData(
                    new LocationMapperUpdateImpl(this));

            while (true) {
                if (dataFetchComplete && locationMapperComplete) {
                    break;
                }
            }

            // fill feed Data Cache
            RecipeDataStore.getsInstance(mContext).getFeedData(null);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            SplashScreenActivity activity = activityWeakReference.get();
            if(activity != null) {
                activity.onDataFetchComplete();
            }
        }

        public void setDataFetchComplete() {
            dataFetchComplete = true;
        }

        public void setLocationMapperTaskComplete() {
            locationMapperComplete = true;
        }
    }

    private static class LocationMapperUpdateImpl implements LocationMapperUpdate {
        SplashAsyncTask splashTask;

        LocationMapperUpdateImpl(SplashAsyncTask task) {
            splashTask = task;
        }

        @Override
        public void onLocationMapperComplete() {
            splashTask.setLocationMapperTaskComplete();
        }
    }

    private static class RecipeDataStoreListenerImpl implements RecipeDataStoreListener {

        SplashAsyncTask splashTask;

        RecipeDataStoreListenerImpl(SplashAsyncTask task) {
            splashTask = task;
        }

        @Override
        public void onDataFetchComplete(List<RecipeInfo> list) {
            splashTask.setDataFetchComplete();
        }

        @Override
        public void onDataUpdate(List<RecipeInfo> list) {

        }
    }
}
