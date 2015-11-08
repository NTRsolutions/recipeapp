package com.automation.crawler;


import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.automation.crawler.MySQLAccess.COLUMNS;
import com.google.gson.Gson;

import au.com.bytecode.opencsv.CSVReader;

import static com.automation.crawler.FoodCategoryList.FoodCategory.*;

public class RecipeCategorisation {
	private static String Seperator = "|";
    private static String sDelimeterPipe = "\\|";
    private static String sDelimeterSpace = " ";
    private static HashMap<String,String> sItemToTagMap;
	RecipeCategorisation(){}
	
	public static void main(String args[]) {
        RecipeCategorisation.startCategorisation();
    }

    public static void startCategorisation() {
        RecipeCategorisation recipeCategorisation = new RecipeCategorisation();

        int batch_size_to_process = 15000;
        MySQLAccess mDatabaseManager = new MySQLAccess();
        String whereClause = " where title is not null";
        try {
            ArrayList<RecipeInfo> list = mDatabaseManager.readDataBase(batch_size_to_process, whereClause);

            // RecipeInfo in info contain's only few data set
            for (RecipeInfo shallowInfo : list) {
                int hashCode = shallowInfo.hash;
                String filePath = CrawlerConfig.BASE_PATH_TO_SAVE_JSON + hashCode + ".json";
                String jsonStr = CrawlerUtility.loadJSONFromFile(filePath);

                Gson gson = new Gson();
                // update info with full data loaded
                RecipeInfo info = gson.fromJson(jsonStr, RecipeInfo.class);

                if (info.category == null || info.category.isEmpty()) { // can be null, as read from json
                    info.category = shallowInfo.category; //replace with DB value's
                }

                String category = info.category;

                category = recipeCategorisation.categoriseAsVeg(info, category);
                category = recipeCategorisation.categoriseAsChicken(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsFish(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsPrawn(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsEgg(info, jsonStr, category);

                category = recipeCategorisation.categoriseAsMutton(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsLamb(info, category);
                category = recipeCategorisation.categoriseAsSalad(info, category);
                category = recipeCategorisation.categoriseAsChutney(info, category);

                category = recipeCategorisation.categoriseAsBreakFast(info, category);
                category = recipeCategorisation.categoriseAsRajasthani(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsPunjabi(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsGujrati(info, jsonStr, category);

                category = recipeCategorisation.categoriseAsBengali(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsParatha(info, category);
                category = recipeCategorisation.categoriseAsDesserts(info, category);
                category = recipeCategorisation.categoriseAsBeverage(info, category);

                category = recipeCategorisation.categoriseAsSoup(info, category);
                category = recipeCategorisation.categoriseAsSauce(info, category);
                category = recipeCategorisation.categoriseAsKerala(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsSouthIndian(info, category);

                category = recipeCategorisation.categoriseAsBaked(info, jsonStr, category);
                category = recipeCategorisation.categoriseAsHealty(info, jsonStr, category);

                try {
                    category = recipeCategorisation.categoriseBasedOnCsv(info, category, getCSVFileData());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                category = recipeCategorisation.categoriseAsNonVeg(info, category);

                if (!category.equalsIgnoreCase("default")) {
                    System.out.println(info.title + " : " + category);
                }

                HashMap<String, String> updateQueryMap = new HashMap<>();
                updateQueryMap.put(COLUMNS.CATEGORY.toString().toLowerCase(), category);
                mDatabaseManager.updateInDb(hashCode, updateQueryMap);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Rule's for categorization ....
	/**************************************** Title Based Categorisation ******************************/
    String categoriseBasedOnCsv(RecipeInfo info, String category, HashMap<String, String> csvMap) {
        for (String csvItems : csvMap.keySet()) {
            if (containedInString(info.title, csvItems, sDelimeterSpace)) {
                category = appendCategory(category, csvMap.get(csvItems));
            }
        }

        return category;
    }

	String categoriseAsSouthIndian(RecipeInfo info, String category) {
		String sItem1 = "kerala";
		String sItem2 = "dosa";
		String sItem3 = "appam";
		String sItem4 = "avial";
		String sItem5 = "sambar";
		String sItem6 = "pongal";
		String sItem7 = "thoran";
		String sItem8 = "kaalan";
		String sItem9 = "koottu";
		String sItem10 = "pachadi";
		
		String southIndian = FoodCategoryList.FoodCategory.SOUTH_INDIAN.getValue();	
		// use only title for segmentation
		if (containedInString(info.title, sItem1, sDelimeterSpace)
				|| containedInString(info.title, sItem1, sDelimeterSpace)
				|| containedInString(info.title, sItem2, sDelimeterSpace)
				|| containedInString(info.title, sItem3, sDelimeterSpace)
				|| containedInString(info.title, sItem4, sDelimeterSpace)
				|| containedInString(info.title, sItem5, sDelimeterSpace)
				|| containedInString(info.title, sItem6, sDelimeterSpace)
				|| containedInString(info.title, sItem7, sDelimeterSpace)
				|| containedInString(info.title, sItem8, sDelimeterSpace)
				|| containedInString(info.title, sItem9, sDelimeterSpace)
				|| containedInString(info.title, sItem10, sDelimeterSpace)) {
			category = appendCategory(category, southIndian);
		}
		
		return category;
	}
	
	// tricky one
	String categoriseAsVeg(RecipeInfo info, String category) {
		String vegItem1 = "paneer";
		String vegItem2 = "gobhi";
		String vegItem3 = "sabji";
		String vegItem4 = "sabzi";
		String vegItem5 = "chole";
		String vegItem6 = "aloo";
		String vegItem7 = "daal";
		String vegItem8 = "rajma";
		String vegItem9 = "veggie";
		String vegItem10 = "vegetable";
		
		String veg = VEGETARIAN.getValue();
		
		// use only title for segmentation
		if (containedInString(info.title, vegItem1, sDelimeterSpace)
                || containedInString(info.title, vegItem2, sDelimeterSpace)
                || containedInString(info.title, vegItem3, sDelimeterSpace)
                || containedInString(info.title, vegItem4, sDelimeterSpace)
                || containedInString(info.title, vegItem5, sDelimeterSpace)
                || containedInString(info.title, vegItem6, sDelimeterSpace)
                || containedInString(info.title, vegItem7, sDelimeterSpace)
                || containedInString(info.title, vegItem8, sDelimeterSpace)
                || containedInString(info.title, vegItem9, sDelimeterSpace)
                || containedInString(info.title, vegItem10, sDelimeterSpace)) {
			category = appendCategory(category, veg);
		}
		
		return category;
	}
	
	String categoriseAsNonVeg(RecipeInfo info, String category) {
		String nonVegItem1 = "chicken";
		String nonVegItem2 = "fish";
		String nonVegItem3 = "egg";
		String nonVegItem4 = "lamb";
		String nonVegItem5 = "beef";
		String nonVegItem6 = "pork";
		String nonVegItem7 = "shrimp";
		String nonVegItem8 = "tuna";
		String nonVegItem9 = "salmon";
		
		String nonVeg = NON_VEGETARIAN.getValue();
		
		// use only title for segmentation
		if (containedInString(info.title, nonVegItem1, sDelimeterSpace)
                || containedInString(info.title, nonVegItem2, sDelimeterSpace)
                || containedInString(info.title, nonVegItem3, sDelimeterSpace)
                || containedInString(info.title, nonVegItem4, sDelimeterSpace)
                || containedInString(info.title, nonVegItem5, sDelimeterSpace)
                || containedInString(info.title, nonVegItem6, sDelimeterSpace)
                || containedInString(info.title, nonVegItem7, sDelimeterSpace)
                || containedInString(info.title, nonVegItem8, sDelimeterSpace)
                || containedInString(info.title, nonVegItem9, sDelimeterSpace)) {

            String vegeterian = VEGETARIAN.getValue();
            if (containedInString(category, vegeterian, sDelimeterPipe)) {
                category = category.replace(vegeterian, nonVeg);
            } else {
                category = appendCategory(category, nonVeg);
            }
		}
		
		return category;
	}
	
	String categoriseAsLamb(RecipeInfo info, String category) {
		String lamb = LAMB.getValue();
		String nonVeg = NON_VEGETARIAN.getValue();
		
		// use only title for segmentation
		if (containedInString(info.title, lamb, sDelimeterSpace)) {
			category = appendCategory(category, lamb);
			category = appendCategory(category, nonVeg);
		}

		return category;
	}
	
	String categoriseAsSalad(RecipeInfo info, String category) {
		String salad = SALAD.getValue();
		// use only title for segmentation
		if (containedInString(info.title, salad, sDelimeterSpace)) {
			category = appendCategory(category, salad);
		}
		
		return category;
	}
	
	String categoriseAsChutney(RecipeInfo info, String category) {
		String chutney = CHUTNEY.getValue();
		// use only title for segmentation
		if (containedInString(info.title, chutney, sDelimeterSpace)) {
			category = appendCategory(category, chutney);
		}
		
		return category;
	}
	
	String categoriseAsSoup(RecipeInfo info, String category) {
		String soupItem1 = " soup"; // space is intentional
		
		String beverage = SOUP.getValue();
		// use only title for segmentation
		if (containedInString(info.title, soupItem1, sDelimeterSpace)) {
			category = appendCategory(category, beverage);
		}
		
		return category;
	}
	
	
	String categoriseAsSauce(RecipeInfo info, String category) {
		String souceItem1 = " sauce"; // space is intentional
		
		String beverage = SAUCE.getValue();
		// use only title for segmentation
		if (containedInString(info.title, souceItem1, sDelimeterSpace)) {
			category = appendCategory(category, beverage);
		}
		
		return category;
	}
	
	
	String categoriseAsBeverage(RecipeInfo info, String category) {
		String bevItem1 = " shake"; // space is intentional
		String bevItem2 = "juice";
		String bevItem3 = "smoothie";		
		String bevItem4 = "drink";	
		String bevItem5 = "lemonade";	
		
		String beverage = BEVERAGE.getValue();
		// use only title for segmentation
		if (containedInString(info.title, bevItem1, sDelimeterSpace)
				|| containedInString(info.title, bevItem2, sDelimeterSpace)
                || containedInString(info.title, bevItem3, sDelimeterSpace)
                || containedInString(info.title, bevItem4, sDelimeterSpace)
                || containedInString(info.title, bevItem5, sDelimeterSpace) ) {
			category = appendCategory(category, beverage);
		}
		
		return category;
	}
	
	String categoriseAsBreakFast(RecipeInfo info, String category) {
		String sandwhich = SANDWHICH.getValue();
		String breakfastItem1 = " shake"; // space is intentional
		String breakfastItem2 = "juice";
		String breakfastItem3 = "smoothie";
		String breakfastItem4 = "pancake";
		
		
		String breakfast = BREAKFAST.getValue();
		// use only title for segmentation
		if (containedInString(info.title, sandwhich, sDelimeterSpace)
				|| containedInString(info.title, breakfastItem1, sDelimeterSpace)
				|| containedInString(info.title, breakfastItem2, sDelimeterSpace)
				|| containedInString(info.title, breakfastItem3, sDelimeterSpace)
				|| containedInString(info.title, breakfastItem4, sDelimeterSpace)) {
			category = appendCategory(category, breakfast);
		}
		
		return category;
	}
		
	String categoriseAsParatha(RecipeInfo info, String category) {
		String paratah = PARATHA.getValue();
		// use only title for segmentation
		if (containedInString(info.title, paratah, sDelimeterSpace)) {
            category = appendCategory(category, paratah);
			category = appendCategory(category, paratah);
		}
		
		return category;
	}
		
	String categoriseAsDesserts(RecipeInfo info, String category) {
		String desserts = DESSERTS.getValue();
		
		String sweetItem1 =  "kheer";
		String sweetItem2 =  "lassi";
		String sweetItem3 =  "cake";
		String sweetItem4 =  "halwa";
		String sweetItem5 =  "phirni";
		String sweetItem6 =  "sweet";
		String sweetItem7 =  "brownies";
		String sweetItem8 =  "mousse";
		String sweetItem9 =  "payasam";
		String sweetItem10 =  "pudding";
		String sweetItem11 =  "shrikhand";
		
		// use only title for segmentation
		if (info.title.toLowerCase().contains(sweetItem1) 
				|| info.title.toLowerCase().contains(sweetItem2) 
				|| info.title.toLowerCase().contains(sweetItem3) 
				|| info.title.toLowerCase().contains(sweetItem4)
				|| info.title.toLowerCase().contains(sweetItem5)
				|| info.title.toLowerCase().contains(sweetItem6)
				|| info.title.toLowerCase().contains(sweetItem7)
				|| info.title.toLowerCase().contains(sweetItem8)
				|| info.title.toLowerCase().contains(sweetItem9)
				|| info.title.toLowerCase().contains(sweetItem10)
				|| info.title.toLowerCase().contains(sweetItem11)) {
			category = appendCategory(category, desserts);  // add to dessert's
		}
		
		return category;
	}
		
	/**************************************** Content Based Categorisation ******************************/	
	String categoriseAsChicken(RecipeInfo info, String jsonStr, String category) {
		String chicken = CHICKEN.getValue();
		String nonVeg = NON_VEGETARIAN.getValue();
		if (jsonStr.toLowerCase().contains(chicken)) {
			category = appendCategory(category, chicken);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	// Rule's for categorization ....
	String categoriseAsFish(RecipeInfo info, String jsonStr, String category) {
		String fish = FISH.getValue();
		String fishType1 = "salmon";
		
		String nonVeg = NON_VEGETARIAN.getValue();
		if (jsonStr.toLowerCase().contains(fish)
				|| jsonStr.toLowerCase().contains(fishType1)) {
			category = appendCategory(category, fish);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsPrawn(RecipeInfo info, String jsonStr, String category) {
		String prawn = PRAWN.getValue();
		String nonVeg = NON_VEGETARIAN.getValue();
		if (jsonStr.toLowerCase().contains(prawn)) {
			category = appendCategory(category, prawn);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsEgg(RecipeInfo info, String jsonStr, String category) {
		String egg = EGG.getValue();
		
		// exception as few recipe has eggless item
		String eggless = "eggless";
		if (jsonStr.toLowerCase().contains(eggless)) { 
			return category;
		}
		
		StringBuilder builder = new StringBuilder();
		for(String s : info.getIngredients()) {
		    builder.append(s);
		}
		String flattenedIngredients = builder.toString();
		
		String nonVeg = NON_VEGETARIAN.getValue();
		if (flattenedIngredients.toLowerCase().contains(egg)) {
			category = appendCategory(category, egg);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsMutton(RecipeInfo info, String jsonStr, String category) {
		String mutton = MUTTON.getValue();
		String nonVeg = NON_VEGETARIAN.getValue();
		if (jsonStr.toLowerCase().contains(mutton)) {
			category = appendCategory(category, mutton);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsRajasthani(RecipeInfo info, String jsonStr, String category) {
		String rajasthani = RAJASTHANI.getValue();
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(rajasthani)) {
			category = appendCategory(category, rajasthani);
		}
		
		return category;
	}
	
	String categoriseAsPunjabi(RecipeInfo info, String jsonStr, String category) {
		String punjabi = PUNJABI.getValue();
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(punjabi)) {
			category = appendCategory(category, punjabi);
		}
		
		return category;
	}
	
	String categoriseAsGujrati(RecipeInfo info, String jsonStr, String category) {
		String gujrati = GUJRATI.getValue();
		String gujratiItem1 = "dhokla";
		String gujratiItem2 = "basundi";
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(gujrati)
				|| jsonStr.toLowerCase().contains(gujratiItem1)
				|| jsonStr.toLowerCase().contains(gujratiItem2)) {
			category = appendCategory(category, gujrati);
		}
		
		return category;
	}
	
	String categoriseAsBengali(RecipeInfo info, String jsonStr, String category) {
		String bengali = BENGALI.getValue();
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(bengali)) {
			category = appendCategory(category, bengali);
		}
		
		return category;
	}
	
	String categoriseAsKerala(RecipeInfo info, String jsonStr, String category) {
		String keralaString = KERALA.getValue();
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(keralaString)) {
			category = appendCategory(category, keralaString);
		}
		
		return category;
	}
	
	String categoriseAsBaked(RecipeInfo info, String jsonStr, String category) {
		String bakedStr = BAKED.getValue();
		String ovenStr = "oven";
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(bakedStr) 
				|| jsonStr.toLowerCase().contains(ovenStr)) {
			category = appendCategory(category, bakedStr);
		}
		
		return category;
	}
	
	String categoriseAsHealty(RecipeInfo info, String jsonStr, String category) {
		String HealthyStr = HEALTHY.getValue();
		String ovenStr = "oven";
		// use only title for segmentation
		List<String> list = info.nutritionList;
		if (list == null || list.size() == 0) {
			return category;
		}
		
		for(String str : list) {
			if (str.toLowerCase().contains("calories")) {
				String val = str.replaceAll("[^.0-9]", "");
				float cal = 100000;
				try {
					cal = Float.parseFloat(val);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (cal < 200) {
					category = appendCategory(category, HealthyStr);
				}
			}
		}
		
		return category;
	}
	/************************************************ Utility Functions *************************************/
	
	boolean containedInString(String originalCategoryList, String newEntry, String delimeter) {
        originalCategoryList = originalCategoryList.trim().toLowerCase();
        newEntry = newEntry.trim().toLowerCase();
		String[] splitCategory = originalCategoryList.split(delimeter);
		for (String category : splitCategory) {
			if (category.equalsIgnoreCase(newEntry)) {
				return true;
			}
		}
		
		return false;
	}
	
	String appendCategory(String originalCategoryList, String newEntry) {
		String finalAppendedCategory = originalCategoryList;
		if (originalCategoryList.equals(DEFAULT.getValue())) {
			return newEntry;
		}
	
		
		if (containedInString(originalCategoryList, newEntry, sDelimeterPipe)) {
			return originalCategoryList;
		}
	
		finalAppendedCategory += ( Seperator + newEntry);
		return finalAppendedCategory;
	}

    // CSV Based Parsing
    private static HashMap<String, String> getCSVFileData() throws Throwable {
        File file = new File("/home/rajnish/auto_tags.csv");
        if (!file.exists()) {
            return null;
        }

        if (sItemToTagMap == null) {
            sItemToTagMap = new HashMap<>();
        } else {
            return sItemToTagMap;
        }

        //create CSVReader object
        CSVReader reader = new CSVReader(new FileReader(file.getPath()), ',');
        //read line by line
        String[] record = null;

        while((record = reader.readNext()) != null){
            String tag = record[0].toLowerCase().trim();
            String items = record[1].toLowerCase().trim();

            String[] split = items.split(",");
            for (String item : split) {
                item = item.trim();
                sItemToTagMap.put(item, tag);
                System.out.println(item + " : " + tag);
            }
        }

        reader.close();

        return sItemToTagMap;
    }
}
