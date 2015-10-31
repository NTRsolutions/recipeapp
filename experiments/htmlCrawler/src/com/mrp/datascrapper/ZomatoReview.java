package com.mrp.datascrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ZomatoReview {
	int id;
	float rating;
	public String text;


	public static List<ZomatoReview> getAllReviewsForId(String restId, boolean showInConsole) {
		try {
			String url = "https://www.zomato.com/php/social_load_more.php";
			String urlParameters = "entity_id="+restId+"&profile_action=reviews-top&page=0&limit="+Config.PAGE_COUNT;
			String htmlData = HttpURLConnection.sendPost(url,urlParameters);
			Document doc = Jsoup.parse(htmlData);
			
			List<ZomatoReview> reviewList = new ArrayList<ZomatoReview>();

			Elements ratingsElements = doc.select("div.rev-text");
			for( Element e : ratingsElements) {
				Element ratingsElement = doc.select("div.left.bold.zdhl2.tooltip").get(0);
				ZomatoReview r = new ZomatoReview();
				r.text = e.text();
				r.rating = getRatingFromElement(ratingsElement);
				if(showInConsole){
					System.out.println(r.text);	
				}
				reviewList.add(r);
			}
			
			return reviewList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ArrayList<ZomatoReview>();
	}
	
	public static float getRatingFromElement(Element e) {
		if(e.classNames().contains("icon-font-level-1"))
				return 1.0f;
		else if(e.classNames().contains("icon-font-level-2"))
			return 1.5f;
		else if(e.classNames().contains("icon-font-level-3"))
			return 2.0f;
		else if(e.classNames().contains("icon-font-level-4"))
			return 2.5f;
		else if(e.classNames().contains("icon-font-level-5"))
			return 3.0f;
		else if(e.classNames().contains("icon-font-level-6"))
			return 3.5f;
		else if(e.classNames().contains("icon-font-level-7"))
			return 4.0f;
		else if(e.classNames().contains("icon-font-level-8"))
			return 4.5f;
		else if(e.classNames().contains("icon-font-level-9"))
			return 5.0f;
		return 1.0f;
	}
}