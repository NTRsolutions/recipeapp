package com.foodie.recipe.data;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by rajnish on 6/11/15.
 */
public class ImageDownloadHelper {
    public static final String TAG = ImageDownloadHelper.class.getSimpleName();
    String objectId;
    int recipeinfoId;
    String title;
    int downloaded;
    int dirty;

    public enum Keys {
        RECIPEINFO_ID,
        TITLE,
        DOWNLOADED,
        DIRTY;

        public String getValue() {
            return this.name().toLowerCase();
        }
    }

    public static ImageDownloadHelper getImageDownloadHelper(ParseObject parseObject) {
        ImageDownloadHelper imageDownloadHelper = new ImageDownloadHelper();
        imageDownloadHelper.recipeinfoId = parseObject.getInt("recipeinfo_id");
        imageDownloadHelper.title = parseObject.getString("title");
        imageDownloadHelper.downloaded = parseObject.getInt("downloaded");
        imageDownloadHelper.dirty = parseObject.getInt("dirty");
        return imageDownloadHelper;
    }


    public static void updateImageDownloadHelper(final RecipeInfo info) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ImageDownloadHelper.class.getSimpleName());
        query.whereEqualTo(ImageDownloadHelper.Keys.RECIPEINFO_ID.getValue(), info.getRecipeinfoId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
                                   public void done(List<ParseObject> results, ParseException e) {
                                       if (results == null || e != null) {
                                           return;
                                       }

                                       if (results != null && results.size() == 0) {
                                           ParseObject parseObject = new ParseObject(
                                                   ImageDownloadHelper.class.getSimpleName());
                                           parseObject.put(RecipeInfoStats.Keys.RECIPEINFO_ID.getValue()
                                                   , info.getRecipeinfoId());
                                           parseObject.put(Keys.TITLE.getValue(), info.getTitle());
                                           parseObject.put(Keys.DOWNLOADED.getValue(), 1);
                                           parseObject.put(Keys.DIRTY.getValue(), 0);
                                           parseObject.saveInBackground();
                                           Log.d(TAG, "zero Result , Creating new Object");
                                           return;
                                       }

                                       ParseObject parseObject = results.get(0);
                                       parseObject.put(Keys.DOWNLOADED.getValue(), 1);
                                       parseObject.put(Keys.DIRTY.getValue(), 0);
                                       parseObject.saveInBackground();

                                   }
                               }
        );
    }

    public static void markDownloadImageAsDirty(final RecipeInfo info) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ImageDownloadHelper.class.getSimpleName());
        query.whereEqualTo(ImageDownloadHelper.Keys.RECIPEINFO_ID.getValue(), info.getRecipeinfoId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
                                   public void done(List<ParseObject> results, ParseException e) {
                                       if (results == null || e != null) {
                                           return;
                                       }

                                       if (results != null && results.size() == 0) {
                                           ParseObject parseObject = new ParseObject(
                                                   ImageDownloadHelper.class.getSimpleName());
                                           parseObject.put(RecipeInfoStats.Keys.RECIPEINFO_ID.getValue()
                                                   , info.getRecipeinfoId());
                                           parseObject.put(Keys.TITLE.getValue(), info.getTitle());
                                           parseObject.put(Keys.DOWNLOADED.getValue(), 0);
                                           parseObject.put(Keys.DIRTY.getValue(), 1);
                                           parseObject.saveInBackground();
                                           Log.d(TAG, "zero Result , Creating new Object");
                                           return;
                                       }

                                       ParseObject parseObject = results.get(0);
                                       parseObject.put(Keys.DOWNLOADED.getValue(), 0);
                                       parseObject.put(Keys.DIRTY.getValue(), 1);
                                       parseObject.saveInBackground();

                                   }
                               }
        );
    }
}
