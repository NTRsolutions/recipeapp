package com.example.recipe;

import android.app.Application;
import android.util.Log;

import com.example.recipe.utility.Config;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by saurabh on 07/08/15.
 */
public class ApplicationImpl extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "BjPG9N2ZepJL2at4Y8267mB5h593H5A89Ianq1T0", "o1C6ZL7kUZkGF0zjuztg9Qz75sSfxx7eWiHjnCRv");

        Config.initialize(this.getApplicationContext());
    }
}
