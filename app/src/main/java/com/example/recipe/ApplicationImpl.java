package com.example.recipe;

import android.app.Application;

import com.example.recipe.utility.Config;

/**
 * Created by saurabh on 07/08/15.
 */
public class ApplicationImpl extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Config.initialize(this.getApplicationContext());
    }
}
