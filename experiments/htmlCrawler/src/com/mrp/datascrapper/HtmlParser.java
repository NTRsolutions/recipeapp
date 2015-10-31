package com.mrp.datascrapper;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.mrp.datamining.FuzzyWordMatcher;

public class HtmlParser {
	
	public static void main(String args[]) {
		try {
			FuzzyWordMatcher.test();
//			FoodPanda.getMenuOfRestaurant("n1gr/wangs-kitchen-koramangala#menu");
//			ArrayList<ZomatoRestaurantInfo> restoList = getZomatoRestaurants("bangalore","koramangala");
//			int i =0;
//			int temporaryLimit = 2; //Don't scrap all.. legal issues
//			for(ZomatoRestaurantInfo resto : restoList) {
//				resto.reviewList = ZomatoReview.getAllReviewsForId(resto.id);
//				if(i>=temporaryLimit)
//					break;
//				i++;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<ZomatoRestaurantInfo> getZomatoRestaurants(String city,String location) {
		ArrayList<ZomatoRestaurantInfo> restInfoList = new ArrayList<ZomatoRestaurantInfo>();
		try {
			String url = "https://www.zomato.com/" + city + "/" + location + "-restaurants" + "?" + "page=" + 1;
			Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
			Elements restaurents = doc.select("li.resZS.mbot0.pbot0.bb.even.status1");
			Elements titles = doc.select("a.result-title");
			
			int i =0;
			for(Element restaurant: restaurents) {
				String id = restaurant.attr("data-res_id");
				Element info = titles.get(i);
				
				ZomatoRestaurantInfo zInfo = new ZomatoRestaurantInfo();
				zInfo.id = id;
				zInfo.name = info.text();
				zInfo.city = city;
				zInfo.location = location;
				zInfo.fullName = info.attr("title");
				zInfo.url = info.attr("href"); 
				
				restInfoList.add(zInfo);
				
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return restInfoList;
	}
	
}



