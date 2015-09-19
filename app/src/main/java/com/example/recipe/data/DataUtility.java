package com.example.recipe.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.recipe.GoogleImageDownloader.GoolgeImageJsonResponse;
import com.example.recipe.GoogleImageDownloader.Result;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 19/9/15.
 */
public class DataUtility {
    public static final String TAG = DataUtility.class.getSimpleName();
    Context mContext;
    private static DataUtility sInstance;

    private DataUtility(Context context) {
        mContext = context;
    }

    public static DataUtility getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataUtility(context);
        }
        return sInstance;
    }

    public File getFilesDir() {
        File fileDir = mContext.getFilesDir();
        return fileDir;
    }

    public String getInternalFilesDirPath() {
        File fileDir = mContext.getFilesDir();
        if (fileDir != null) {
            return fileDir.getAbsolutePath();
        }
        return null;
    }

    public String getExternalFilesDirPath() {
        File fileDir = mContext.getExternalFilesDir(null);
        if (fileDir != null) {
            return fileDir.getAbsolutePath();
        }
        return null;
    }

    public String loadJSONFromFile(String filePath) {
        StringBuffer stringBuffer = new StringBuffer();
        String aDataRow = "";
        try {
            File myFile = new File(filePath);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));

            while ((aDataRow = myReader.readLine()) != null) {
                stringBuffer.append(aDataRow + "\n");
            }
            myReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();

    }

    public HashMap<String, ArrayList<String>> getImageUrlMapList() {
        return mImageMapList;
    }


    //// TODO: 19/9/15  (rkumar) Debug code to remove later
    HashMap<String, ArrayList<String>> mImageMapList = new HashMap<>();


    //// TODO: 19/9/15  (rkumar) Debug code to remove later
    public void networkImageRequest(final String docId, String query) {
        String url = String.format("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&rsz=8", query);
        url = url.replace(" ", "+");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jsonString = response.toString();
                    Gson gson = new Gson();
                    GoolgeImageJsonResponse goolgeImageJsonResponse = gson.fromJson(jsonString,
                            GoolgeImageJsonResponse.class);
                    List<Result> results = goolgeImageJsonResponse.getResponseData().getResults();
                    ArrayList<String> imageUrlList =  new ArrayList<>();
                    for (Result result : results) {
                        imageUrlList.add(result.getUrl());
                    }
                    Log.d(TAG, "onResponse ");
                    mImageMapList.put(docId, imageUrlList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse ");
            }
        });
        NetworkUtility.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }
}
