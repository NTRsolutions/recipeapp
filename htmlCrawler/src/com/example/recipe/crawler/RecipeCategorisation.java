package com.example.recipe.crawler;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.example.recipe.crawler.MySQLAccess.COLUMNS;
import com.google.gson.Gson;

public class RecipeCategorisation {
	private static String Seperator = "|";
	RecipeCategorisation(){}
	
	public static void main(String args[]) {
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
				category = recipeCategorisation.categoriseAsVeg(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsNonVeg(info, jsonStr, category);
				
				category = recipeCategorisation.categoriseAsChicken(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsFish(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsPrawn(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsEgg(info, jsonStr, category);
				
				category = recipeCategorisation.categoriseAsMutton(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsLamb(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsSalad(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsChutney(info, jsonStr, category);
				
				category = recipeCategorisation.categoriseAsBreakFast(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsRajasthani(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsPunjabi(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsGujrati(info, jsonStr, category);
				
				category = recipeCategorisation.categoriseAsBengali(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsParatha(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsDesserts(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsBeverage(info, jsonStr, category);
				
				category = recipeCategorisation.categoriseAsSoup(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsSauce(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsKerala(info, jsonStr, category);
				category = recipeCategorisation.categoriseAsSouthIndian(info, jsonStr, category);
				
				
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
	
	String categoriseAsSouthIndian(RecipeInfo info, String jsonStr, String category) {
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
		if (info.title.toLowerCase().contains(sItem1)
				|| info.title.toLowerCase().contains(sItem1)
				|| info.title.toLowerCase().contains(sItem2)
				|| info.title.toLowerCase().contains(sItem3)
				|| info.title.toLowerCase().contains(sItem4)
				|| info.title.toLowerCase().contains(sItem5)
				|| info.title.toLowerCase().contains(sItem6)
				|| info.title.toLowerCase().contains(sItem7)
				|| info.title.toLowerCase().contains(sItem8)
				|| info.title.toLowerCase().contains(sItem9)
				|| info.title.toLowerCase().contains(sItem10)) {
			category = appendCategory(category, southIndian);
		}
		
		return category;
	}
	
	// tricky one
	String categoriseAsVeg(RecipeInfo info, String jsonStr, String category) {
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
		
		String veg = FoodCategoryList.FoodCategory.VEGETARIAN.getValue();
		
		// use only title for segmentation
		if (info.title.toLowerCase().contains(vegItem1)
				|| info.title.toLowerCase().contains(vegItem2)
				|| info.title.toLowerCase().contains(vegItem3)
				|| info.title.toLowerCase().contains(vegItem4)
				|| info.title.toLowerCase().contains(vegItem5)
				|| info.title.toLowerCase().contains(vegItem6)
				|| info.title.toLowerCase().contains(vegItem7)
				|| info.title.toLowerCase().contains(vegItem8)
				|| info.title.toLowerCase().contains(vegItem9)
				|| info.title.toLowerCase().contains(vegItem10)) {
			category = appendCategory(category, veg);
		}
		
		return category;
	}
	
	String categoriseAsNonVeg(RecipeInfo info, String jsonStr, String category) {
		String nonVegItem1 = "chicken";
		String nonVegItem2 = "fish";
		String nonVegItem3 = "egg";
		String nonVegItem4 = "lamb";
		String nonVegItem5 = "beef";
		String nonVegItem6 = "pork";
		String nonVegItem7 = "shrimp";
		String nonVegItem8 = "tuna";
		String nonVegItem9 = "salmon";
		
		String nonVeg = FoodCategoryList.FoodCategory.NON_VEGETARIAN.getValue();
		
		// use only title for segmentation
		if (info.title.toLowerCase().contains(nonVegItem1)
				|| info.title.toLowerCase().contains(nonVegItem2)
				|| info.title.toLowerCase().contains(nonVegItem3)
				|| info.title.toLowerCase().contains(nonVegItem4)
				|| info.title.toLowerCase().contains(nonVegItem5)
				|| info.title.toLowerCase().contains(nonVegItem6)
				|| info.title.toLowerCase().contains(nonVegItem7)
				|| info.title.toLowerCase().contains(nonVegItem8)
				|| info.title.toLowerCase().contains(nonVegItem9)) {
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsLamb(RecipeInfo info, String jsonStr, String category) {
		String lamb = FoodCategoryList.FoodCategory.LAMB.getValue();
		String nonVeg = FoodCategoryList.FoodCategory.NON_VEGETARIAN.getValue();
		
		// use only title for segmentation
		if (info.title.toLowerCase().contains(lamb)) {
			category = appendCategory(category, lamb);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsSalad(RecipeInfo info, String jsonStr, String category) {
		String salad = FoodCategoryList.FoodCategory.SALAD.getValue();		
		// use only title for segmentation
		if (info.title.toLowerCase().contains(salad)) {
			category = appendCategory(category, salad);
		}
		
		return category;
	}
	
	String categoriseAsChutney(RecipeInfo info, String jsonStr, String category) {
		String chutney = FoodCategoryList.FoodCategory.CHUTNEY.getValue();		
		// use only title for segmentation
		if (info.title.toLowerCase().contains(chutney)) {
			category = appendCategory(category, chutney);
		}
		
		return category;
	}
	
	String categoriseAsSoup(RecipeInfo info, String jsonStr, String category) {
		String soupItem1 = " soup"; // space is intentional
		
		String beverage = FoodCategoryList.FoodCategory.SOUP.getValue();	
		// use only title for segmentation
		if (info.title.toLowerCase().contains(soupItem1)) {
			category = appendCategory(category, beverage);
		}
		
		return category;
	}
	
	
	String categoriseAsSauce(RecipeInfo info, String jsonStr, String category) {
		String souceItem1 = " sauce"; // space is intentional
		
		String beverage = FoodCategoryList.FoodCategory.SAUCE.getValue();	
		// use only title for segmentation
		if (info.title.toLowerCase().contains(souceItem1)) {
			category = appendCategory(category, beverage);
		}
		
		return category;
	}
	
	
	String categoriseAsBeverage(RecipeInfo info, String jsonStr, String category) {
		String bevItem1 = " shake"; // space is intentional
		String bevItem2 = "juice";
		String bevItem3 = "smoothie";		
		String bevItem4 = "drink";	
		String bevItem5 = "lemonade";	
		
		String beverage = FoodCategoryList.FoodCategory.BEVERAGE.getValue();	
		// use only title for segmentation
		if (info.title.toLowerCase().contains(bevItem1) 
				|| info.title.toLowerCase().contains(bevItem2)
				|| info.title.toLowerCase().contains(bevItem3)
				|| info.title.toLowerCase().contains(bevItem4)
				|| info.title.toLowerCase().contains(bevItem5)) {
			category = appendCategory(category, beverage);
		}
		
		return category;
	}
	
	String categoriseAsBreakFast(RecipeInfo info, String jsonStr, String category) {
		String sandwhich = FoodCategoryList.FoodCategory.SANDWHICH.getValue();	
		String breakfastItem1 = " shake"; // space is intentional
		String breakfastItem2 = "juice";
		String breakfastItem3 = "smoothie";
		String breakfastItem4 = "pancake";
		
		
		String breakfast = FoodCategoryList.FoodCategory.BREAKFAST.getValue();	
		// use only title for segmentation
		if (info.title.toLowerCase().contains(sandwhich) 
				|| info.title.toLowerCase().contains(breakfastItem1)
				|| info.title.toLowerCase().contains(breakfastItem2)
				|| info.title.toLowerCase().contains(breakfastItem3)
				|| info.title.toLowerCase().contains(breakfastItem4)) {
			category = appendCategory(category, breakfast);
		}
		
		return category;
	}
		
	String categoriseAsParatha(RecipeInfo info, String jsonStr, String category) {
		String paratah = FoodCategoryList.FoodCategory.PARATHA.getValue();		
		// use only title for segmentation
		if (info.title.toLowerCase().contains(paratah)) {
			category = appendCategory(category, paratah);
			category = appendCategory(category, paratah);
		}
		
		return category;
	}
		
	String categoriseAsDesserts(RecipeInfo info, String jsonStr, String category) {
		String desserts = FoodCategoryList.FoodCategory.DESSERTS.getValue();	
		
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
		String chicken = FoodCategoryList.FoodCategory.CHICKEN.getValue();
		String nonVeg = FoodCategoryList.FoodCategory.NON_VEGETARIAN.getValue();
		if (jsonStr.toLowerCase().contains(chicken)) {
			category = appendCategory(category, chicken);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	// Rule's for categorization ....
	String categoriseAsFish(RecipeInfo info, String jsonStr, String category) {
		String fish = FoodCategoryList.FoodCategory.FISH.getValue();
		String fishType1 = "salmon";
		
		String nonVeg = FoodCategoryList.FoodCategory.NON_VEGETARIAN.getValue();
		if (jsonStr.toLowerCase().contains(fish)
				|| jsonStr.toLowerCase().contains(fishType1)) {
			category = appendCategory(category, fish);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsPrawn(RecipeInfo info, String jsonStr, String category) {
		String prawn = FoodCategoryList.FoodCategory.PRAWN.getValue();
		String nonVeg = FoodCategoryList.FoodCategory.NON_VEGETARIAN.getValue();
		if (jsonStr.toLowerCase().contains(prawn)) {
			category = appendCategory(category, prawn);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsEgg(RecipeInfo info, String jsonStr, String category) {
		String egg = FoodCategoryList.FoodCategory.EGG.getValue();
		
		// exception as few recipe has eggless item
		String eggless = "eggless";
		if (jsonStr.toLowerCase().contains(eggless)) { 
			return category;
		}
		
		StringBuilder builder = new StringBuilder();
		for(String s : info.getIngredients()) {
		    builder.append(s);
		}
		String flattenedIngredients =  builder.toString();
		
		String nonVeg = FoodCategoryList.FoodCategory.NON_VEGETARIAN.getValue();
		if (flattenedIngredients.toLowerCase().contains(egg)) {
			category = appendCategory(category, egg);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsMutton(RecipeInfo info, String jsonStr, String category) {
		String mutton = FoodCategoryList.FoodCategory.MUTTON.getValue();
		String nonVeg = FoodCategoryList.FoodCategory.NON_VEGETARIAN.getValue();
		if (jsonStr.toLowerCase().contains(mutton)) {
			category = appendCategory(category, mutton);
			category = appendCategory(category, nonVeg);
		}
		
		return category;
	}
	
	String categoriseAsRajasthani(RecipeInfo info, String jsonStr, String category) {
		String rajasthani = FoodCategoryList.FoodCategory.RAJASTHANI.getValue();		
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(rajasthani)) {
			category = appendCategory(category, rajasthani);
		}
		
		return category;
	}
	
	String categoriseAsPunjabi(RecipeInfo info, String jsonStr, String category) {
		String punjabi = FoodCategoryList.FoodCategory.PUNJABI.getValue();		
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(punjabi)) {
			category = appendCategory(category, punjabi);
		}
		
		return category;
	}
	
	String categoriseAsGujrati(RecipeInfo info, String jsonStr, String category) {
		String gujrati = FoodCategoryList.FoodCategory.GUJRATI.getValue();	
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
		String bengali = FoodCategoryList.FoodCategory.BENGALI.getValue();		
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(bengali)) {
			category = appendCategory(category, bengali);
		}
		
		return category;
	}
	
	String categoriseAsKerala(RecipeInfo info, String jsonStr, String category) {
		String keralaString = FoodCategoryList.FoodCategory.KERALA.getValue();		
		// use only title for segmentation
		if (jsonStr.toLowerCase().contains(keralaString)) {
			category = appendCategory(category, keralaString);
		}
		
		return category;
	}
	
	/************************************************ Utility Functions *************************************/
	
	boolean containedInList(String originalCategoryList, String newEntry) {
		String[] splitCategory = originalCategoryList.split("\\|");
		for (String category : splitCategory) {
			if (category.equalsIgnoreCase(newEntry)) {
				return true;
			}
		}
		
		return false;
	}
	
	String appendCategory(String originalCategoryList, String newEntry) {
		String finalAppendedCategory = originalCategoryList;
		if (originalCategoryList.equals(FoodCategoryList.FoodCategory.DEFAULT.getValue())) {
			return newEntry;
		}
	
		
		if (containedInList(originalCategoryList, newEntry)) {
			return originalCategoryList;
		}
	
		finalAppendedCategory += ( Seperator + newEntry);
		return finalAppendedCategory;
	}

}
