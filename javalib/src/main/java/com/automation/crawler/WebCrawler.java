package com.automation.crawler;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.automation.crawler.MySQLAccess.COLUMNS;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by root on 10/9/15.
 */
/**
 * @author rajnish
 * 
 */
public class WebCrawler {
	public static final String BASE_PATH_TO_SAVE_JSON = "/home/rajnish/ParseCloudeCode/recipe/public/json/";
	HashMap<Integer, String> touchedUrl = new HashMap<>(10000);
	public static final String TAG = "WebCrawler";
	Logger mLogger = Logger.getLogger(this.getClass().getSimpleName());
	// public static final String URL =
	// "http://allrecipes.co.in/recipe/379/print-friendly.aspx";
	public static final String URL = "http://allrecipes.co.in";
	public static final String BASE_URL = "http://allrecipes.co.in/";
	public static final String PROBABLE_RECEPIE_ITEM_URL_PREFIX = "http://allrecipes.co.in/recipe/";
	private static WebCrawler sInstance;
	MySQLAccess mDatabaseManager;

	public static WebCrawler getInstance() {
		if (sInstance == null) {
			sInstance = new WebCrawler();
		}

		return sInstance;
	}

	private WebCrawler() {
		mDatabaseManager = new MySQLAccess();
	};

	public static void main(String args[]) {
		WebCrawler webcrawler = WebCrawler.getInstance();
		webcrawler.startCrawler();
	}

	public void startCrawler() {
		parseListUrl();
		// extractLinks(URL);
		// dumpDataLinearly();
	}

	// ***************************************************** URL Parsing
	// **************************************/

	public void parseListUrl() {
		String whereClause = " where dirty = 0 and done = 0";
		ArrayList<RecipeInfo> list = null;
		try {
			// DB row's primary data, not updated yet
			list = mDatabaseManager.readDataBase(12950, whereClause);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.out.println("mDatabaseManager read fail");
			return;
		}
		
		for (int i = 0; i < list.size(); i++) {
			String url = list.get(i).getBaseUrl();
			int hashCode = url.hashCode();
			
			
			
			// Updated RecipeInfo from parsed data
			RecipeInfo recipeDescription = null;
			try {
				recipeDescription = parseSingleUrl(list.get(i).getBaseUrl());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
				HashMap<String, String> map = new HashMap<>();
				map.put(COLUMNS.DIRTY.toString().toLowerCase(), 1 + "");
				try {
					mDatabaseManager.updateInDb(hashCode, map);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}

			
			
			Gson gson = new Gson();
			String json = gson.toJson(recipeDescription,
					RecipeInfo.class);			
			try {
				String filename = BASE_PATH_TO_SAVE_JSON + hashCode + ".json";
				PrintWriter out = new PrintWriter(filename);
				out.println(json);
				out.close();
				
				String title = cleanStringBeforeDbQuery(recipeDescription.title);
				String description = cleanStringBeforeDbQuery(recipeDescription.description);
				String cookingTime = cleanStringBeforeDbQuery(recipeDescription.preparationTime);
				String serving = cleanStringBeforeDbQuery(recipeDescription.serves);
				
				
				assert(title != null);
				assert(description != null);
				
				if (cookingTime == null) {
					cookingTime = "";
				}

				if (serving == null) {
					serving = "";
				}
				
				HashMap<String, String> map = new HashMap<>();
				map.put(COLUMNS.TITLE.toString().toLowerCase(), title.trim());
				map.put(COLUMNS.DESCRIPTION.toString().toLowerCase(), description.trim());
				map.put(COLUMNS.COOKING_TIME.toString().toLowerCase(), cookingTime.trim());
				map.put(COLUMNS.SERVING.toString().toLowerCase(), serving.trim());
				map.put(COLUMNS.DONE.toString().toLowerCase(), 1 + "");
				mDatabaseManager.updateInDb(hashCode, map);
				
			} catch (Exception e) {
				System.out.println(json);
				e.printStackTrace();
			}
		}
	}

	public String cleanStringBeforeDbQuery(String str) {
		if (str == null) {
			return str;
		}
		
		str = str.replace("\\u0027", "'"); // replace unicode char
		str = str.replace("\\\"", "'"); // replace unicode char
		str = str.replace("\"", "'"); // replace unicode char
		return str;
	} 
	/**
	 * @param url
	 * @throws IOException 
	 */
	public RecipeInfo parseSingleUrl(String url) throws IOException {
		RecipeInfo recipeDescription = new RecipeInfo();
		Document doc = null;
		doc = Jsoup.connect(url).timeout(5 * 1000).get();

		if (doc == null) {
			throw new IOException();
		}
		
		Elements elements = doc.getElementsByClass("fullContainer");
		ArrayList<String> parsedList = new ArrayList<String>();
		for (Element element : elements) {
			recurseElemet(element, parsedList);
		}

		ArrayList<String> toCleanList = new ArrayList<String>();
		for (int i = 0; i < parsedList.size(); i++) {
			int jump = mergeParts(parsedList, i, toCleanList);
			i += jump;
		}

		// for (int i = 0; i < toCleanList.size(); i++) {
		// System.out.println(toCleanList.get(i));
		// }

		Element titleElement = doc.getElementById("lblTitle");
		ArrayList<String> titleList = new ArrayList<String>();
		List<TextNode> tNodes = titleElement.textNodes();
		for (TextNode tNode : tNodes) {
			printTextNode(tNode, titleList);
		}

		if (titleList.size() > 0) {
			System.out
					.println("*********************  Title found *********************");
			System.out.println(titleList.get(0));
			recipeDescription.title = titleList.get(0);
		}

		String desc = extractDescription(toCleanList);
		if (desc != null) {
			System.out
					.println("\n *********************  Desc Found ******************** \n");
			System.out.println(desc);
			recipeDescription.description = desc;
		}

		String readyIn = extractReadyIn(toCleanList);
		if (readyIn != null) {
			System.out
					.println("\n *********************  Ready In******************** \n");
			System.out.println(readyIn);
			recipeDescription.preparationTime = readyIn;
		}

		String serves = extractServes(doc);
		if (serves != null) {
			System.out
					.println("\n *********************  serves******************** \n");
			System.out.println(serves);
			recipeDescription.serves = serves;
		}

		ArrayList<String> ingredientList = extractIngredientList(doc);
		if (ingredientList != null && ingredientList.size() > 0) {
			System.out
					.println("\n *********************  Ingredient Found ******************** \n");
			for (int i = 0; i < ingredientList.size(); i++) {
				System.out.println(ingredientList.get(i));
			}

			recipeDescription.ingredients = ingredientList;
		}

		ArrayList<String> prepList = extractPreperationListList(toCleanList);
		if (prepList != null && prepList.size() > 0) {
			System.out
					.println("\n *********************  Prep List Found ******************** \n");
			for (int i = 0; i < prepList.size(); i++) {
				System.out.println(prepList.get(i));
			}

			recipeDescription.directions = prepList;
		}

		ArrayList<String> nutritionList = extractNutrition(toCleanList);
		if (nutritionList != null && nutritionList.size() > 0) {
			System.out
					.println("\n *********************  Nutrition Found ******************** \n");
			for (int i = 0; i < nutritionList.size(); i++) {
				System.out.println(nutritionList.get(i));
			}

			recipeDescription.nutritionList = nutritionList;
		}

		return recipeDescription;
	}

	public String extractReadyIn(ArrayList<String> originalList) {
		String readyin = null;
		for (int i = 0; i < originalList.size(); i++) {
			if (originalList.get(i).trim().startsWith("Ready in")) {
				readyin = originalList.get(i).trim();
				break;
			}
		}
		return readyin;
	}

	public String extractServes(Document doc) {
		String serves = null;
		Elements elements = doc.getAllElements();
		for (Element element : elements) {
			String str = element.text().trim().toLowerCase();
			if (!isDirtyText(str)) {
				if (str.startsWith("serves")
						&& str.length() < 2 * "serves".length()) {
					serves = str;
					break;
				}
			}
		}
		return serves;
	}

	public ArrayList<String> extractPreperationListList(
			ArrayList<String> originalList) {
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < originalList.size(); i++) {
			if (originalList.get(i).trim()
					.equalsIgnoreCase("Preparation method")) {
				int count = i + 1;
				String temp = originalList.get(count).trim();
				while (temp.length() > 20 && count < originalList.size()) {
					list.add(temp);
					if ((count + 1) == originalList.size()) {
						break;
					}

					count++;
					temp = originalList.get(count).trim();
				}
			}
		}
		return list;
	}

	public ArrayList<String> extractIngredientList(Document doc) {
		ArrayList<String> list = new ArrayList<>();
		Elements ingredienElements = doc.getElementsByClass("ingredient");
		for (Element element : ingredienElements) {
			list.add(element.text());
		}
		return list;
	}

	public ArrayList<String> extractNutrition(ArrayList<String> originalList) {
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < originalList.size(); i++) {
			String temp = originalList.get(i).trim();
			if (temp.equalsIgnoreCase("Nutrition")) {
				int count = i + 1;
				while (count < originalList.size()) {
					list.add(originalList.get(count).trim());
					count++;
				}
			}
		}

		return list;
	}

	public String extractDescription(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			int count = i;
			if (list.get(i).trim().equalsIgnoreCase("Ingredients")) {
				String temp = "";
				while (temp.length() < 100 && count > 0) {
					temp = list.get(count);
					count--;
				}

				if (!temp.equalsIgnoreCase("")) {
					return temp;
				}
			}
		}

		return null;
	}

	public int mergeParts(ArrayList<String> originalList, int index,
			ArrayList<String> condensedList) {
		if (originalList.get(index).trim().equals("Recipe by:")) {
			String currentStr = originalList.get(index);
			currentStr += (" " + originalList.get(index + 1));
			currentStr += (" " + originalList.get(index + 2));
			currentStr.replace("\n", "");
			// condensedList.add(currentStr);
			return 3;
		}

		if (originalList.get(index).trim().endsWith("Prep:")) {
			return 0;
		}

		if (originalList.get(index).trim().endsWith("Cook:")) {
			return 0;
		}

		if (originalList.get(index).trim().equals("Ready in")) {
			String currentStr = originalList.get(index);
			currentStr += (" " + originalList.get(index + 1));
			currentStr.replace("\n", "");
			condensedList.add(currentStr);
			return 1;
		}

		if (originalList.get(index).trim().equals("Preparation method")) {
			String currentStr = originalList.get(index);
			String tmp = originalList.get(index + 1);

			int skipCount = 0;
			while (!tmp.trim().startsWith("1.")) {
				skipCount++;
				tmp = originalList.get(index + skipCount);
			}

			currentStr.replace("\n", "");
			condensedList.add(currentStr);
			return skipCount > 0 ? skipCount - 1 : 0;
		}

		if (originalList.get(index).trim().equals("Serves")) {
			String currentStr = originalList.get(index);
			int retIndex = 0;
			if (originalList.get(index + 1).trim().length() < 4) {
				currentStr += (" " + originalList.get(index + 1));
				retIndex++;
			}

			currentStr.replace("\n", "");
			condensedList.add(currentStr);
			return retIndex;
		}

		if (originalList.get(index).trim().equals("Allrecipes")) {
			String currentStr = "Description";
			condensedList.add(currentStr);
			return 0;
		}

		if (originalList.get(index).trim().length() == 2
				&& originalList.get(index).trim().substring(0, 1)
						.matches("[-+]?\\d*\\.?\\d+")
				&& originalList.get(index).trim().substring(1, 2).equals(".")) {
			String currentStr = originalList.get(index);
			currentStr += (" " + originalList.get(index + 1));
			currentStr.replace("\n", "");
			condensedList.add(currentStr);
			return 1;
		}

		condensedList.add(originalList.get(index));
		return 0;
	}

	public void recurseElemet(Element element, ArrayList<String> parsedList) {
		Elements elements = element.children();
		for (Element el : elements) {
			List<TextNode> tNodes = el.textNodes();

			for (TextNode tNode : tNodes) {
				printTextNode(tNode, parsedList);
			}
			recurseElemet(el, parsedList);
		}
	}

	private boolean isDirtyText(String txt) {
		if (txt.equals("\n")) {
			return true;
		}

		if (txt.replace(" ", "").length() == 1) {
			return true;
		}

		if (txt.equals(" ")) {
			return true;
		}

		if (txt.equals(":")) {
			return true;
		}

		if (txt.contains("ALL RIGHTS RESERVED")) {
			return true;
		}

		if (txt.contains("Last updated")) {
			return true;
		}

		if (txt.contains("Back to:")) {
			return true;
		}

		if (txt.contains("Print")) {
			return true;
		}

		if (txt.contains("Back to:")) {
			return true;
		}

		if (txt.contains("Provided by:")) {
			return true;
		}

		if (txt.contains("Photo by:")) {
			return true;
		}

		if (txt.contains("Found in:")) {
			return true;
		}

		return false;
	}

	private void printTextNode(TextNode tNode, ArrayList<String> parsedList) {
		if (tNode == null) {
			return;
		}

		if (isDirtyText(tNode.text())) {
			return;
		}

		parsedList.add(tNode.text());
		System.out.println("\n Node Data |" + tNode.text());
	}

	// ***************************************************** URL Crawling
	// **************************************/
	private boolean isDirtyURL(String url) {
		if (!url.contains(BASE_URL)) {
			return true;
		}

		if (url.contains("#")) {
			return true;
		}

		if (url.endsWith(".jpg") || url.endsWith(".jpeg")) {
			return true;
		}

		if (!url.startsWith(PROBABLE_RECEPIE_ITEM_URL_PREFIX)) {
			return true;
		}

		return false;
	}

	ArrayList<String> getUrlList(String url) {
		ArrayList<String> list = new ArrayList<>();
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(10 * 1000).get();
		} catch (Exception e) {
			return list;
		}

		Elements links = doc.select("a[href]");
		for (Element link : links) {
			String urlFound = link.attr("abs:href");
			list.add(urlFound);
		}

		return list;
	}

	public void extractLinks(String url) throws Exception {
		int hashCode = url.hashCode();
		if (touchedUrl.get(hashCode) != null) {
			return;
		}

		touchedUrl.put(hashCode, url);
		mLogger.info("Touched Url Count " + touchedUrl.size()
				+ "\n processing : " + url);

		final ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> urlList = getUrlList(url);
		for (int i = 0; i < urlList.size(); i++) {
			String urlFound = urlList.get(i);
			int hashCodeInternal = urlFound.hashCode();

			if (!urlFound.startsWith(BASE_URL)) {
				continue;
			}

			if (urlFound.contains("searchresults")
					|| urlFound.contains("search-results")
					|| urlFound.contains("cooks")) {
				continue;
			}

			if (touchedUrl.get(hashCodeInternal) != null) {
				continue;
			} else {
				// processing
				if (!isDirtyURL(urlFound)) {
					mLogger.info("extractLinks | " + urlFound);
					try {
						mDatabaseManager.insertInDb(hashCodeInternal, urlFound);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
				// recursion
				extractLinks(urlFound);
			}
		}

		return;
	}

	public void dumpDataLinearly() {
		int lowerLimit = 50;
		int upperLimit = 13000;
		for (int i = lowerLimit; i < upperLimit; i++) {
			String url = String.format(
					"http://allrecipes.co.in/recipe/%d/print-friendly.aspx", i);
			try {
				extractLinksLinear(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void extractLinksLinear(String urlFound) throws Exception {
		mLogger.info("\n processing : " + urlFound);
		int hashCodeInternal = urlFound.hashCode();
		if (!urlFound.startsWith(BASE_URL)) {
			return;
		}

		// processing
		if (!isDirtyURL(urlFound)) {
			mLogger.info("extractLinks | " + urlFound);
			try {
				mDatabaseManager.insertInDb(hashCodeInternal, urlFound);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return;
	}
}
