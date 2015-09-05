package com.example.recipe.utility;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by rajnish on 10/8/15.
 */
public class Util {

    public static String readDataFromFile(String fileName) {
        File file = new File(fileName);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
