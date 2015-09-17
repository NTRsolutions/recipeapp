package com.mrp.datascrapper;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodPandaMenu {
	int restId;
	
	HashMap<String ,ArrayList<MenuSubItems>> list = new HashMap<>();
	
	public void setMenuList(HashMap<String ,ArrayList<MenuSubItems>>  list){
		this.list = list;
	}
	
	public class MenuSubItems{
		String menuItemTitle;
		String menuItemDescription;
		ArrayList<MenuSubItemVariation> menuSubItemVariation = new ArrayList<>();
		
	public MenuSubItems(String title, String price){
			this.menuItemTitle = title;
			this.menuItemDescription = price;
			
		}
	
	public void setMenuSubItemVariation(ArrayList<MenuSubItemVariation> list){
		menuSubItemVariation = list;
		}
	
	} 
	
	public class MenuSubItemVariation{
		String menuItemVariationTitle;
		String menuItemVariationPrice;
		
		public MenuSubItemVariation(String title, String price){
			menuItemVariationTitle = title;
			menuItemVariationPrice = price;
		}
	}
}
