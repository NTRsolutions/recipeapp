package com.example.recipe.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.example.recipe.crawler.ParseExportedData.ParseCloudSchema;
import com.google.gson.Gson;

public class ParserUtilCategoryUpdate {
	public enum CSV_COLUMNS {
		RECIPEINFO_ID, TITLE, DESCRIPTION, COOKING_TIME, SERVING, CATEGORY
	}
	
	public ParserUtilCategoryUpdate() {
		
	}
	
	public static void main(String args[]) {
		ParserUtilCategoryUpdate util = new ParserUtilCategoryUpdate();
		String csvFilePath = "/home/rajnish/RecipeInfo.json";
		
		try {
			util.updateCategoryFromExportedData(csvFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private static void updateCategoryFromExportedData(String parseExportedData) throws Exception {
    	
    	String jSonString = loadJSONFromFile(parseExportedData);
        Gson gson = new Gson();
        ParseExportedData desc = gson.fromJson(jSonString, ParseExportedData.class);
    	
		MySQLAccess mDatabaseManager = new MySQLAccess();
        List<ParseCloudSchema>  RecipeInfoList = desc.getRecipeInfo();
        for (ParseCloudSchema info : RecipeInfoList) {
        	int hashCode = info.recipeinfo_id;
        	String category = info.category;
			HashMap<String, String> updateQueryMap = new HashMap<>();
			updateQueryMap.put(CSV_COLUMNS.CATEGORY.toString().toLowerCase(), category);
			mDatabaseManager.updateInDb(hashCode, updateQueryMap);
			System.out.println("category updated : " + hashCode + " : " 
					+ info.title + " : " + category);
        }
    }
    
    public static String loadJSONFromFile(String filePath) {
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
}
