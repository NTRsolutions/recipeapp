package com.foodie.recipe.data;

import android.os.AsyncTask;

public class UnzipFiles extends AsyncTask<String, String, String> {
    public static final String TAG = UnzipFiles.class.getSimpleName();

    public interface UnzipFilesListener {
        void onUnzipComplete();
    }

    String mFinalePath;
    String mTempDownloadPath;
    UnzipFilesListener mListener;

    public UnzipFiles(String tempDownloadPath, String finalPath, UnzipFilesListener listner) {
        mFinalePath = finalPath;
        mTempDownloadPath = tempDownloadPath;
        mListener = listner;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;

        Decompress d = new Decompress(mTempDownloadPath, mFinalePath);
        d.unzip();

        return mFinalePath;
    }

    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
    }

    @Override
    protected void onPostExecute(String file_url) {
        if (mListener != null) {
            mListener.onUnzipComplete();
        }
    }

}