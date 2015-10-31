package com.automation.crawler;

import com.automation.crawler.MySQLAccess.COLUMNS;

import java.io.FileReader;
import java.util.HashMap;


import au.com.bytecode.opencsv.CSVReader;
public class CSVParserUtil {
	public enum CSV_COLUMNS {
		RECIPEINFO_ID, TITLE, DESCRIPTION, COOKING_TIME, SERVING, CATEGORY
	}
	
	public CSVParserUtil() {
		
	}
	
	public static void main(String args[]) {
		CSVParserUtil util = new CSVParserUtil();
		String csvFilePath = "/home/rajnish/RecipeInfo.csv";
		
		try {
			util.parseCSVFileLineByLine(csvFilePath);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private static void parseCSVFileLineByLine(String csvFilePath) throws Throwable {
        //create CSVReader object
        CSVReader reader = new CSVReader(new FileReader(csvFilePath), ',');
		MySQLAccess mDatabaseManager = new MySQLAccess();
        //read line by line
        String[] record = null;
        //skip header row
        reader.readNext();
         
        while((record = reader.readNext()) != null){
        	String hashCodeStr = record[CSV_COLUMNS.RECIPEINFO_ID.ordinal()];
        	String title = record[CSV_COLUMNS.TITLE.ordinal()];
        	String category = record[CSV_COLUMNS.CATEGORY.ordinal()];
        	
        	int hashCode = Integer.parseInt(hashCodeStr);
			HashMap<String, String> updateQueryMap = new HashMap<>();
			updateQueryMap.put(COLUMNS.CATEGORY.toString().toLowerCase(), category);
			mDatabaseManager.updateInDb(hashCode, updateQueryMap);
			System.out.println("category updated : " + hashCodeStr + " : " 
					+ title + " : " + category);
        }
        
        reader.close();
    }
}
