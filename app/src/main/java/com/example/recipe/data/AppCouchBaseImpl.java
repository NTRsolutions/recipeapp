package com.example.recipe.data;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import junit.framework.Assert;

import java.io.IOException;

/**
 * Created by root on 27/8/15.
 */

public class AppCouchBaseImpl {
    private static AppCouchBaseImpl sInstance = null;
    private Database mTagsDatabase;
    private Database mVideoInfoDatabase;
    private Manager manager;
    private Context mContext;

    public enum DatabaseType {
        RECIPE_DATA_STORE
    }

    public static AppCouchBaseImpl getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppCouchBaseImpl(context.getApplicationContext());
            if (sInstance.manager == null) {
                sInstance = null;
            }
        }
        return sInstance;
    }

    private AppCouchBaseImpl(Context cntx){
        try {
            mContext = cntx;
            manager = getManagerInstance();
        } catch (Exception ex) {
            manager = null;
        }
    }

    public Database getDatabaseInstance(DatabaseType type) throws CouchbaseLiteException {
        Assert.assertNotNull(mContext);
        Assert.assertNotNull(manager);
        Database database = null;
        switch (type) {
            case RECIPE_DATA_STORE:
                if ((this.mTagsDatabase == null)) {
                    this.mTagsDatabase = manager.getDatabase(type.name().toLowerCase());
                }
                database = mTagsDatabase;
                break;
        }

        return database;
    }

    private Manager getManagerInstance() throws IOException {
        Assert.assertNotNull(mContext);
        if (manager == null) {
            manager = new Manager(new AndroidContext(mContext), Manager.DEFAULT_OPTIONS);
        }
        return manager;
    }
}
