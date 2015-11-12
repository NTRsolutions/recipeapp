package com.foodie.recipe.data;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;

import com.foodie.recipe.data.UnzipFiles.UnzipFilesListener;
import com.foodie.recipe.utility.AppPreference;
import com.foodie.recipe.utility.Utility;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AssetDataCopyService extends Service {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    private boolean mTaskComplete;

    public AssetDataCopyService() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            Utility.copyAssets(this);
            String tempPath = DataUtility.getInstance(this).getExternalFilesDirPath()
                    + "/json.zip";
            String finalPath =  DataUtility.getInstance(this).getExternalFilesDirPath()
                    + "/" ;
            UnzipFiles downloadAndUnzipFile = new UnzipFiles(tempPath, finalPath, new UnzipFilesListenerImpl());
            downloadAndUnzipFile.execute("UnzipFiles");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class UnzipFilesListenerImpl implements UnzipFilesListener {

        @Override
        public void onUnzipComplete() {
            AppPreference.getInstance(AssetDataCopyService.this)
                    .putBoolean(RecipeDataStore.kJsonDownloadedKey, true);
           stopSelf();
        }
    }
}

