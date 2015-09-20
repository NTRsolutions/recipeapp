package com.example.recipe.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileFromURL extends AsyncTask<String, String, String> {
    public static final String TAG = DownloadFileFromURL.class.getSimpleName();
    WeakReference<Context> mContextWeakReference;
    String mUrl = "";
    String mFinalePath;

    public DownloadFileFromURL(Context context, String url, String finalPath) {
        mContextWeakReference = new WeakReference<>(context);
        mUrl = url;
        mFinalePath = finalPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        URL url = null;
        try {
            url = new URL(mUrl);
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
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return url.toString();
    }

    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
    }

    @Override
    protected void onPostExecute(String file_url) {
        Context cntx = mContextWeakReference.get();

        if (cntx == null) {
            Log.d(TAG, "Context null");
            return;
        }

        if (file_url != null) {
            Toast.makeText(cntx, "downloaded : " + file_url, Toast.LENGTH_LONG);
        } else {
            Toast.makeText(cntx, "downloaded : Failed", Toast.LENGTH_LONG);
        }
    }

}