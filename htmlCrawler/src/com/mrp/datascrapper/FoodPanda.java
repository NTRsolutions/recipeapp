package com.mrp.datascrapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mrp.datascrapper.FoodPandaMenu.MenuSubItemVariation;
import com.mrp.datascrapper.FoodPandaMenu.MenuSubItems;

public class FoodPanda {
	public static FoodPandaMenu getMenuOfRestaurant(String urlSuffix){
		FoodPandaMenu menu = new FoodPandaMenu();
		HashMap<String ,ArrayList<MenuSubItems>>  menuList = new HashMap<>();
		try {
			String url = "https://www.foodpanda.in/restaurant/"+urlSuffix;
			Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
			Elements menuCategory = doc.select("article.menu__category");
			for(Element res: menuCategory) {
				String foodTitle = res.getElementsByClass("menu__category__title").size() > 0 ? res.getElementsByClass("menu__category__title").get(0).text() : "" ; 
				System.out.println(foodTitle);
				
				Elements menu_item__content_wrappers = res.getElementsByClass("menu-item__content-wrapper");
				ArrayList<MenuSubItems> menuItemVar = new ArrayList<>();
				for (Element menu_item__content_wrapper : menu_item__content_wrappers) {
					String title = menu_item__content_wrapper.getElementsByClass("menu-item__title").size() > 0 ? menu_item__content_wrapper.getElementsByClass("menu-item__title").get(0).text() : "";
					System.out.println("TITLE  :" + title);
					String desc = menu_item__content_wrapper.getElementsByClass("menu-item__description").size() > 0 ? menu_item__content_wrapper.getElementsByClass("menu-item__description").get(0).text() : ""; 
					System.out.println("DESC  :" +desc);
					
					Elements priceList = menu_item__content_wrapper.getElementsByClass("menu-item__variation");
					
					MenuSubItems item = menu.new MenuSubItems(title, desc);
					
					ArrayList<MenuSubItemVariation> menuSubItemVariationList = new ArrayList<>();
					MenuSubItemVariation menuSubItemVariation = null;
					for (Element pElements : priceList) {
						String vTitle = pElements.getElementsByClass("menu-item__variation__title").size() > 0 ? pElements.getElementsByClass("menu-item__variation__title").get(0).text() : "";
						System.out.println("V Title : "+ vTitle);
						String vPrice = pElements.getElementsByClass("menu-item__variation__price").size() > 0 ? pElements.getElementsByClass("menu-item__variation__price").get(0).text() : "";
						System.out.println("V Price : "+ vPrice);
						menuSubItemVariation = menu.new MenuSubItemVariation(vTitle, vPrice);
						menuSubItemVariationList.add(menuSubItemVariation);

					}
					item.setMenuSubItemVariation(menuSubItemVariationList);
					menuItemVar.add(item);
				}
				
				menuList.put(foodTitle, menuItemVar);
			}
		
		}catch(Exception e){
			e.printStackTrace();
			
		}
		menu.setMenuList(menuList);
		return menu;
	}
}