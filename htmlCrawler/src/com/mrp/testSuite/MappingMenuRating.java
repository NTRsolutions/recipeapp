package com.mrp.testSuite;

import java.util.ArrayList;
import java.util.List;

import com.mrp.datamining.FuzzyWordMatcher;
import com.mrp.datascrapper.Config;
import com.mrp.datascrapper.FoodPanda;
import com.mrp.datascrapper.FoodPandaMenu;
import com.mrp.datascrapper.ZomatoReview;

public class MappingMenuRating {
	
	List<ZomatoReview> goodReviewList = new ArrayList<>();
	List<ZomatoReview> badReviewList = new ArrayList<>();
	List<ZomatoReview> newtralReviewList = new ArrayList<>();
	
	public static void main(String args[]) {
		MappingMenuRating mapping = new MappingMenuRating();
		mapping.fetchMenuAndReviewList();
	}
	
	public void fetchMenuAndReviewList(){
		try {
			FoodPandaMenu menu = FoodPanda.getMenuOfRestaurant("n1gr/tadka-singh-indiranagar#menu");
			List<ZomatoReview> list = ZomatoReview.getAllReviewsForId("56009", false);
			segmentReview(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void segmentReview(List<ZomatoReview> list){
		for(ZomatoReview review : list){
			String menuWord = "chicken Tikka";
			float match = FuzzyWordMatcher.matchFoundInSentence(review.text.toLowerCase(), menuWord);
			System.out.println("match:##" + match);
			if(match==1){System.out.println(review.text.toLowerCase());}
		}
	}
}
