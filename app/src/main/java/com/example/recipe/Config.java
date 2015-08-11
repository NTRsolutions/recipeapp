package com.example.recipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.os.Environment;

/**
 * Created by rajnish on 10/8/15.
 */
public class Config {

    public static final String SEPERATOR = "/";
    public static final String APP_NAME = "RecipeApp";
    public static final String FOLDER_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + SEPERATOR + APP_NAME;

    java.io.File xmlFile = new java.io.File(Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + "/Filename.xml");

    public static void initializeApp(){
        java.io.File  appRecipe = new File(FOLDER_PATH);
        if (!appRecipe.exists()) {
            appRecipe.mkdir();
        }
    }
}
