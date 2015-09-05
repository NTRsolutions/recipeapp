package com.example.recipe.utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Config {
    public static int DEVICE_DENSITY_DPI;
    public static float DEVICE_DENSITY;
    public static Point SCREEN_SIZE = new Point();
    public static Point FULL_SCREEN_SIZE = new Point();
    public static float MAX_CATEGORY_CARD_HEIGHT_PECENTAGE = 0.40f;
    public static Context APPLICATION_CONTEXT = null;

    public static void initialize(Context context){
        setDeviceDensityDpi(context);
        setDeviceDensity(context);
        setScreenSize(context);
        setApplicationContext(context);
    }

    private static void setDeviceDensityDpi(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        DEVICE_DENSITY_DPI = metrics.densityDpi;
    }

    private static void setDeviceDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        DEVICE_DENSITY = metrics.density;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void populateSize(Display display){
        display.getRealSize(FULL_SCREEN_SIZE);
    }

    private static void setFullScreenSize( Display display) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            populateSize(display);
        } else {
            display.getSize(FULL_SCREEN_SIZE);
        }
    }
    private static void setScreenSize(Context cntx){
        Point screenSize = new Point();
        WindowManager manager = (WindowManager) cntx.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        display.getSize(screenSize);
        SCREEN_SIZE = screenSize;
        setFullScreenSize(display);
    }

    private static void setApplicationContext(Context context) {
        APPLICATION_CONTEXT = context.getApplicationContext();
    }
}
