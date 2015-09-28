package com.example.recipe.data;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Reducer;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.example.recipe.utility.AppPreference;
import com.example.recipe.utility.Config;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeDataStore {
    public static final String TAG = "RecipeDataStore";
    public static final String kRecipeInfoTimeLapseView = "kRecipeInfoTimeLapseView";
    public static final String kRecipeInfoView = "kRecipeInfoView";
    public static final String kSearchTagsView = "kSearchTagsView";
    public static final String kJsonDownloadedKey = "kJsonDownloadedKey";
    private boolean isJsonZipDownloaded;
    private static RecipeDataStore sInstance;
    static List<RecipeInfo> sRecipeInfoList;
    static List<RecipeInfo> sFavouriteRecipeInfoList;
    private int singleBatchLimit = 1000;
    private Gson mGson;
    Database mDataBase;
    Context mContext;

    public enum RecipeCategoryType {
        FEED,
        CATEGORY,
        FAVOURITE,
        HISTORY
    }

    public interface RecipeDataStoreListener {
       void onDataFetchComplete(List<RecipeInfo> list);
        void onDataUpdate(List<RecipeInfo> list);
    }

    public static RecipeDataStore getsInstance(Context cntx) {
        if (sInstance == null) {
            sInstance = new RecipeDataStore(cntx);
        }

        return sInstance;
    }

    private RecipeDataStore(Context cntx) {
        mContext = cntx;
        try {
            mDataBase = AppCouchBaseImpl.getsInstance(mContext)
                    .getDatabaseInstance(AppCouchBaseImpl.DatabaseType.RECIPE_DATA_STORE);
            mGson = new Gson();
            sRecipeInfoList = new ArrayList<>();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return;
        }

        createViews();
        sFavouriteRecipeInfoList = searchDocuments(Config.sFavouriteTag, 1000);
    }

    // create View's for search.
    private void createViews() {
        createRecipeInfoView();
        createRecipeInfoTimeLapseView();
        createSearchView();
    }

    private void createSearchView() {
        View searchView = mDataBase.getView(kSearchTagsView);
        searchView.setMapReduce(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                RecipeInfo info = recipeFromJsonMap(document);
                String category = info.getCategory();
                if (category != null) {
                    String[] categories = category.split("\\|");
                    int numTags = categories.length;
                    for (int i = 0; i < numTags; ++i) {
                        emitter.emit(categories[i], info);
                    }
                }

                List<String> additionalTags = info.getTags();
                if (additionalTags != null) {
                    for (String tag : additionalTags) {
                        emitter.emit(tag, jsonMapFromRecipeInfo(info));
                    }
                }
            }
        }, new Reducer() {
            @Override
            public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
                if (rereduce) {
                    return View.totalValues(values);
                } else {
                    return values.size();
                }
            }
        }, "1"  /*version. Increment me after each change in this map function*/);
    }

    private void createRecipeInfoView() {
        View searchView = mDataBase.getView(kRecipeInfoView);
        searchView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                RecipeInfo info = recipeFromJsonMap(document);
                // this is done for querying all videos in sorted order during load time
                emitter.emit(info.getDocId(), jsonMapFromRecipeInfo(info));
            }
        }, "1"  /*version. Increment me after each change in this map function*/);
    }

    private void createRecipeInfoTimeLapseView() {
        View searchView = mDataBase.getView(kRecipeInfoTimeLapseView);
        searchView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                RecipeInfo info = recipeFromJsonMap(document);
                Long dateTaken = info.getLastViewedTime();

                // this is done for querying all videos in sorted order during load time
                emitter.emit(dateTaken, jsonMapFromRecipeInfo(info));
            }
        }, "1"  /*version. Increment me after each change in this map function*/);
    }

    public List<RecipeInfo> searchDocuments(String searchTag, int num) {
        Query query = mDataBase.getView(kSearchTagsView).createQuery();
        List<Object> keys = new ArrayList<>();
        keys.add(searchTag);
        query.setKeys(keys);
        query.setLimit(num);
        query.setMapOnly(true);
        QueryEnumerator result;
        try {
            result = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return null;
        }

        List<RecipeInfo> infoList = new ArrayList<>();
        for (Iterator<QueryRow> it = result; it.hasNext();) {
            QueryRow row = it.next();
            Log.d(TAG, row.getDocumentId());
            Document doc = row.getDocument();
            Map<String, Object> properties = doc.getProperties();
            RecipeInfo recipeInfo = recipeFromJsonMap(properties);
            infoList.add(recipeInfo);
        }

        Log.v(TAG, "Searched for docs with tag: " + searchTag + ". Found docs: " + infoList);
        return infoList;
    }

    public void addFavouriteTextTags(RecipeInfo info) {
        for (RecipeInfo recipeInfo : sFavouriteRecipeInfoList) {
            if (recipeInfo.getRecipeinfoId() == info.getRecipeinfoId()) {
                //alredyAdded;
                return;
            }
        }

        RecipeDataStore.getsInstance(mContext).addFreeTextTag(
                info, Config.sFavouriteTag);
        sFavouriteRecipeInfoList.add(info);
    }

    public void removeFavouriteTextTags(RecipeInfo info) {
        boolean found = false;
        for (RecipeInfo recipeInfo : sFavouriteRecipeInfoList) {
            if (recipeInfo.getRecipeinfoId() == info.getRecipeinfoId()) {
                found = true;
                break;
            }
        }

        RecipeDataStore.getsInstance(mContext).removeFreeTextTag(
                info, Config.sFavouriteTag);
        sFavouriteRecipeInfoList.remove(info);
    }

    public boolean isFavouriteTextTag(RecipeInfo info) {
        boolean found = false;
        for (RecipeInfo recipeInfo : sFavouriteRecipeInfoList) {
            if (recipeInfo.getRecipeinfoId() == info.getRecipeinfoId()) {
                found = true;
                break;
            }
        }
        return found;
    }

    public void addFreeTextTags(RecipeInfo info, final Collection<String> freeTextTags) {
        // Wil create a document with key: mediaId if one doesn't exist as yet.
        String documentId = info.getDocId();
        Document doc = mDataBase.getDocument(documentId);
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();

                    RecipeInfo recipeInfo = recipeFromJsonMap(properties);
                    if (recipeInfo.getTags() == null) {
                        recipeInfo.setTags(new ArrayList<String>());
                    }

                    recipeInfo.getTags().addAll(freeTextTags);
                    newRevision.setUserProperties(jsonMapFromRecipeInfo(recipeInfo));

                    Log.v(TAG, "Added free tags. tags: " + mGson.toJson(recipeInfo));
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addFreeTextTag(RecipeInfo info, final String freeTextTag) {
        addFreeTextTags(info, Collections.singleton(freeTextTag));
    }

    public void removeFreeTextTags(RecipeInfo info, final Collection<String> freeTextTags) {
        final String documentId = info.getDocId();
        // Wil create a document with key: mediaId if one doesn't exist as yet.
        Document doc = mDataBase.getDocument(documentId);
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();

                    RecipeInfo recipeInfo = recipeFromJsonMap(properties);
                    if (recipeInfo.getTags() == null) {
                        Log.w(TAG, "removeFreeTextTags() null list for mediaId: " + documentId);
                        return false;
                    }
                    boolean modified = recipeInfo.getTags().removeAll(freeTextTags);
                    if (!modified) {
                        Log.w(TAG, "removeFreeTextTags() tags not found for mediaId: " + documentId
                                + ", current tags: " + recipeInfo.getTags()
                                + ", removal request: " + freeTextTags);
                        return false;
                    }

                    newRevision.setUserProperties(jsonMapFromRecipeInfo(recipeInfo));
                    Log.v(TAG, "removed free tags. tags: " + mGson.toJson(recipeInfo));
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFreeTextTag(RecipeInfo info, final String freeTextTag) {
        removeFreeTextTags(info, Collections.singleton(freeTextTag));
    }

    public void getRecipeList(RecipeCategoryType type, RecipeDataStoreListener listener) {
        switch (type) {
            case FEED:
                getFeedData(listener);
                break;
            case CATEGORY:
                break;
            case FAVOURITE:
                getFavouriteData(listener);
                break;
            case HISTORY:
                getHistoryData(listener);
                break;
        }
    }

    private void getFavouriteData(RecipeDataStoreListener listener) {
        sFavouriteRecipeInfoList = searchDocuments(Config.sFavouriteTag, 1000);
        listener.onDataFetchComplete(sFavouriteRecipeInfoList);
    }

    private void getHistoryData(RecipeDataStoreListener listener) {
        List<RecipeInfo>  list = getRecenetRecipeInfos();
        listener.onDataFetchComplete(list);
    }

    private void getFeedData(RecipeDataStoreListener listener) {
        if (sRecipeInfoList.size() == 0) {
            getAllVideoInfos();
        }

        if (listener != null && sRecipeInfoList.size() > 0) {
            listener.onDataFetchComplete(sRecipeInfoList);
            return;
        }

        // DB is empty, fetch from cloud
        fetchAllInfoData(listener);
    }

    // Reads the CB db and returns getRecenetInfos
    private List<RecipeInfo> getRecenetRecipeInfos() {
        List<RecipeInfo> infoList = new ArrayList<>();
        Query query = mDataBase.getView(kRecipeInfoTimeLapseView).createQuery();
        query.setDescending(true);
        query.setLimit(100); // set hundred limit
        QueryEnumerator result ;
        try {
            result = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return null;
        }

        for (Iterator<QueryRow> it = result; it.hasNext();) {
            QueryRow row = it.next();
            Document doc = row.getDocument();
            Map<String, Object> properties = doc.getProperties();
            RecipeInfo recipeInfo = recipeFromJsonMap(properties);
            Long key = (Long) row.getKey();
            if (key != null && key > 0) {
                Log.d(TAG, row.getKey() + " | " + recipeInfo.getDocId());
                infoList.add(recipeInfo);
            }
        }

        return infoList;
    }

    // Reads the CB db and returns all the videos.
    private void getAllVideoInfos() {
        Query query = mDataBase.getView(kRecipeInfoView).createQuery();
        query.setDescending(true);
        QueryEnumerator result ;
        try {
            result = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return;
        }

        for (Iterator<QueryRow> it = result; it.hasNext();) {
            QueryRow row = it.next();
            Document doc = row.getDocument();
            Map<String, Object> properties = doc.getProperties();
            RecipeInfo recipeInfo = recipeFromJsonMap(properties);
            Log.d(TAG, row.getKey() + " | " + recipeInfo.getDocId());
            addVideoInfoToCache(recipeInfo);
        }
    }

    private void addVideoInfoToCache(RecipeInfo info) {
        sRecipeInfoList.add(info);
    }



    public void updateDoc(RecipeInfo info) {
        String documentId = info.getDocId();
        final Map<String, Object> updateProperties = jsonMapFromRecipeInfo(info);
        Document doc = mDataBase.getDocument(documentId);
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> newProperties = newRevision.getUserProperties();
                    // Add updated properties.
                    newProperties.putAll(updateProperties);
                    // Commit the updated doc to DB.
                    newRevision.setUserProperties(newProperties);
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fetchAllInfoData(final RecipeDataStoreListener listener) {
        if (listener != null && sRecipeInfoList.size() > 0) {
            listener.onDataFetchComplete(sRecipeInfoList);
            return;
        }

        ParseQuery<ParseObject> category = ParseQuery.getQuery(RecipeInfo.class.getSimpleName());
        category.setLimit(singleBatchLimit);
        category.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                List<RecipeInfo> list = new ArrayList<>();
                for (ParseObject object : results) {
                    RecipeInfo category = RecipeInfo.getRecipeInfo(object);
                    updateDoc(category);
                    Log.d(TAG, "got result ");
                    list.add(category);
                }
                if (listener != null) {
                    sRecipeInfoList = list;
                    listener.onDataFetchComplete(list);
                }
            }
        });

    }

    public void checkAndDownloadJsonData() {
        isJsonZipDownloaded = AppPreference.getInstance(mContext)
                .getBoolean(kJsonDownloadedKey, false);

        if (!isJsonZipDownloaded) {
            String url = "http://virtualcook.parseapp.com/json/json.zip";
            String tempPath = DataUtility.getInstance(mContext).getExternalFilesDirPath()
                    + "/json.zip";
            String finalPath =  DataUtility.getInstance(mContext).getExternalFilesDirPath()
                    + "/" + "json/";
            DownloadAndUnzipFile downloadAndUnzipFile = new DownloadAndUnzipFile(
                    url, tempPath, finalPath);
            downloadAndUnzipFile.execute("DownloadAndUnzipFile");

            AppPreference.getInstance(mContext)
                    .putBoolean(kJsonDownloadedKey, true);
        }
    }
    public Map<String, Object> jsonMapFromRecipeInfo(RecipeInfo info) {
        String jsonStr = mGson.toJson(info);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap = mGson.fromJson(jsonStr, jsonMap.getClass());
        return jsonMap;
    }

    private RecipeInfo recipeFromJsonMap(Map<String, Object> properties) {
        String jsonStr = mGson.toJson(properties);
        return mGson.fromJson(jsonStr, RecipeInfo.class);
    }

    public void dispose() {
        sInstance = null;
    }

}
