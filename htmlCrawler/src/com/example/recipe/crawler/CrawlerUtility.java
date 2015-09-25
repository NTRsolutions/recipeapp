package com.example.recipe.crawler;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerUtility {

	public static ArrayList<String> getUrlList(String url) {
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
	
}
