package com.example.recipe.data;

import android.os.AsyncTask;
import android.util.Log;

import com.example.recipe.ui.RecipeDetailFragment.TaskCompletion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by rajnish on 13/10/15.
 */
public class DownloadDescFromUrl extends AsyncTask<String,String,Boolean> {
    public static final String TAG = "DownloadDescFromUrl";
    WeakReference<TaskCompletion> mContextWeakReference;

    String mUrl = "";
    String mFinalePath;

    public DownloadDescFromUrl(TaskCompletion holder, String url, String finalPath) {
        mContextWeakReference = new WeakReference<>(holder);
        mUrl = url;
        mFinalePath = finalPath;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        int count;
        URL url = null;
        try {
            url = new URL(mUrl);
            String parentDir = mFinalePath.substring(0, mFinalePath.lastIndexOf("/"));
            File file = new File(parentDir);
            if (!file.exists()) {
                file.mkdirs();
            }

            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            OutputStream output = new FileOutputStream(mFinalePath);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return false;
        }
         return true;

    }

    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
    }

    @Override
    protected void onPostExecute(Boolean result) {
        TaskCompletion holder = mContextWeakReference.get();

        if (result == null || result == false) {
            Log.d(TAG, "Download Failed for : " + mFinalePath);
        } else {
            Log.d(TAG, "Download complete for : " + mFinalePath);
        }

        if (holder != null) {
            holder.onTaskCompletionResult(result, mFinalePath);
        }
    }
}
