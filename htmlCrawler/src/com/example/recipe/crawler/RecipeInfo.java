package com.foodie.recipe.crawler;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 13/9/15.
 */
public class RecipeInfo implements Serializable{
	public String baseUrl;
	public int hash;
	public String title;
    public String description;
    public String imageUrl;
    public List<String> ingredients;
    public List<String> directions;
    public List<String> nutritionList;
    public String serves;
    public String preparationTime;
  

//    public static RecipeDescription getRecipeDescription() {
//        String json = Util.jsonData;
//        Gson gson = new Gson();
//        RecipeDescription desc = gson.fromJson(json, RecipeDescription.class);
//        return desc;
//    }

    public String getTitle() {
        return title;
    }



    public String getImageUrl() {
        return imageUrl;
    }



    public List<String> getIngredients() {
        return ingredients;
    }



    public List<String> getDirections() {
        return directions;
    }



    public String getServes() {
        return serves;
    }


    public String getPreparationTime() {
        return preparationTime;
    }
    
    public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public List<String> getNutritionList() {
		return nutritionList;
	}



	public void setNutritionList(List<String> nutritionList) {
		this.nutritionList = nutritionList;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}



	public void setIngredients(List<String> ingredients) {
		this.ingredients = ingredients;
	}



	public void setDirections(List<String> directions) {
		this.directions = directions;
	}



	public void setServes(String serves) {
		this.serves = serves;
	}



	public void setPreparationTime(String preparationTime) {
		this.preparationTime = preparationTime;
	}

}
