package com.foodie.recipe.data;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 1/11/15.
 */
public class ParseImageDownloadHelper extends IntentService {
    public static final String TAG = ParseImageDownloadHelper.class.getSimpleName();
    public static final int sSingleBatchLimit = 1000;
    public static int sDownloadedImagesSkipLength = 0;
    public static int sDirtyImagesSkipLength = 0;

    public ParseImageDownloadHelper() {
        super("ParseImageDownloadHelper");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startImageDownloadHelperSanity();
    }

    void startImageDownloadHelperSanity() {

        int downloadedImageResultCount = Integer.MAX_VALUE;
        sDownloadedImagesSkipLength += fetchDownloadedImageData();
        while (downloadedImageResultCount > 0) {
            downloadedImageResultCount = fetchDownloadedImageData();
            sDownloadedImagesSkipLength += downloadedImageResultCount;
        }

        int dirtyImageResultCount = Integer.MAX_VALUE;
        sDirtyImagesSkipLength += fetchDirtyImageData();
        while (dirtyImageResultCount > 0) {
            dirtyImageResultCount = fetchDirtyImageData();
            sDirtyImagesSkipLength += dirtyImageResultCount;
        }
    }

    public int fetchDownloadedImageData() {
        List<ParseObject> results = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ImageDownloadHelper.class.getSimpleName());
        try {
            query.setLimit(sSingleBatchLimit);
            query.setSkip(sDownloadedImagesSkipLength);
            query.whereEqualTo("downloaded", 1);
            results = query.find();
            for (ParseObject object : results) {
                ImageDownloadHelper imageDownloadHelper = ImageDownloadHelper.getImageDownloadHelper(object);
                Log.d(TAG, "Image downloaded so deleting " + imageDownloadHelper.title);
                RecipeDataStore.getsInstance(ParseImageDownloadHelper.this).deleteDocument(
                        imageDownloadHelper.recipeinfoId + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return results.size();
    }


    public int fetchDirtyImageData() {
        List<ParseObject> results = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ImageDownloadHelper.class.getSimpleName());
        try {
            query.setLimit(sSingleBatchLimit);
            query.setSkip(sDirtyImagesSkipLength);
            query.whereEqualTo("dirty", 1);
            results = query.find();
            for (ParseObject object : results) {
                ImageDownloadHelper imageDownloadHelper = ImageDownloadHelper.getImageDownloadHelper(object);
                Log.d(TAG, "Image Dirty so deleting " + imageDownloadHelper.title);
                RecipeDataStore.getsInstance(ParseImageDownloadHelper.this).deleteDocument(
                        imageDownloadHelper.recipeinfoId + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return results.size();
    }
}
