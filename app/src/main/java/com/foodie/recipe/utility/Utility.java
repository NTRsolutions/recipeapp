package com.foodie.recipe.utility;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.foodie.recipe.data.RecipeInfo;
import com.facebook.rebound.SpringSystem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Utility {
    public static String TAG = "Utility";
    private static List<String> mContactsTags = null;
    private static SpringSystem sSpringSystem;

    Context mContext;
    private TextToSpeech mTextToSpeech;
    private static Utility sInstance;

    private Utility(Context context) {
        mContext = context.getApplicationContext();
        mTextToSpeech = new TextToSpeech(mContext,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                    }
                }
        );
    }

    public static Utility getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Utility(context);
        }
        return sInstance;
    }

    public void init() {

    }

    public static int dpToPixels(int dps) {
        final float scale = Config.DEVICE_DENSITY;
        return (int) (dps * scale + 0.5f);
    }

    public static float convertPixelsToDp(float px){
        float dp = (px/ Config.DEVICE_DENSITY) + 0.5f;
        return dp;
    }

    public static String toCamelCase(String s){
        if (s.isEmpty()) {
            return s;
        }
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts){
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        if (s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    public static String millisecondToTimeString(long milliSeconds) {
        long seconds = milliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long remainderMinutes = minutes - (hours * 60);
        long remainderSeconds = seconds - (minutes * 60);

        StringBuilder timeStringBuilder = new StringBuilder();

        // Hours
        if (hours > 0) {
            if (hours < 10) {
                timeStringBuilder.append('0');
            }
            timeStringBuilder.append(hours);

            timeStringBuilder.append(':');
        }

        // Minutes
        if (remainderMinutes < 10) {
            timeStringBuilder.append('0');
        }
        timeStringBuilder.append(remainderMinutes);
        timeStringBuilder.append(':');

        // Seconds
        if (remainderSeconds < 10) {
            timeStringBuilder.append('0');
        }
        timeStringBuilder.append(remainderSeconds);

        return timeStringBuilder.toString();
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void shareVideoIntent(Context cntx, String mediaPath){
        final String type = "video/*";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(type);
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        cntx.startActivity(Intent.createChooser(share, "Share to"));
    }

    public static SpringSystem getSpringSystem() {

        if(sSpringSystem != null) {
            return sSpringSystem;
        }

        sSpringSystem = SpringSystem.create();
        return sSpringSystem;
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(View view) {
        if (view != null) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public enum DateFormat {
        FULL,
        YEAR,
        MONTH,
        DAY,
        TIME,
        DAY_OF_YEAR,
    }

    public static String getUtcTimeFromEpochTime(long epochTime, DateFormat dateFormat) {
        java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        switch (dateFormat) {
            case FULL:
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case YEAR:
                formatter = new SimpleDateFormat("yyyy");
                break;
            case MONTH:
                formatter = new SimpleDateFormat("MM");
                break;
            case DAY:
                formatter = new SimpleDateFormat("dd");
                break;
            case TIME:
                formatter = new SimpleDateFormat("HH:mm:ss");
                break;
            case DAY_OF_YEAR:
                formatter = new SimpleDateFormat("MMdd");
                break;
        }

        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date= new Date(epochTime);
        String dateString = formatter.format(date);
        return dateString;
    }


    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private static String[] getCategories(String categoryStr) {
        String[] categories = categoryStr.split("\\|");
        return categories;
    }

    public static String[] getCategories(RecipeInfo info) {
        String categoryStr = info.getCategory();

        if (categoryStr == null || categoryStr.equalsIgnoreCase("")) {
            return null;
        }

        String[] categories = Utility.getCategories(categoryStr);
        return categories;
    }

    public TextToSpeech getTextToSpeech() {
        return mTextToSpeech;
    }

    public void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }

    public static class ValueComparator implements Comparator<String> {
        Map base;

        public ValueComparator(Map base) {
            this.base = base;
        }

        @Override
        public int compare(String lhs, String rhs) {
            if ((int)base.get(lhs) >= (int)base.get(rhs)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
}

