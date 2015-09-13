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

    public static String jsonData = "{\n" +
            "  \"title\": \"Chicken Hariyali Tikka\",\n" +
            "  \"imageUrl\": \"http://allrecipes.co.in/recipe/634/print-friendly.aspx\",\n" +
            "  \"ingredients\": [\n" +
            "    \"half a bunch fresh coriander\",\n" +
            "    \"10 mint leaves\",\n" +
            "    \"Garlic Cloves\"\n" +
            "  ],\n" +
            "  \"directions\": [\n" +
            "    \"d  alf a bunch fresh coriander\",\n" +
            "    \"d 10 mint leaves\",\n" +
            "    \"d Garlic Cloves\"\n" +
            "  ],\n" +
            "  \"serves\": \"3-4\",\n" +
            "  \"preparationTime\": \"30-40 min\"\n" +
            "} ";


}
