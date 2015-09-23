package com.example.recipe.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rajnish on 22/9/15.
 */
public class ShoppingListDataStore implements Serializable{
    public static final String SHOPPING_IST_DATA_STORE_KEY = "SHOPPING_IST_DATASTORE__KEY";
    ArrayList<ShoppingItemInfo> list = new ArrayList<>();

    private static ShoppingListDataStore sInstance;

    private ShoppingListDataStore() {}

    public static ShoppingListDataStore getInstance() {
        if (sInstance == null) {
            sInstance = new ShoppingListDataStore();
        }
        return sInstance;
    }

    public ArrayList<ShoppingItemInfo> getList() {
        return list;
    }

    public void setList(ArrayList<ShoppingItemInfo> list) {
        this.list = list;
    }

    public static void updateShoppingList(RecipeDescription recipeDescription, String ingredientItem) {
        String title = recipeDescription.getTitle();
        List<String> recipeIngredientList = recipeDescription.getIngredients();
        ShoppingListDataStore listInstance = ShoppingListDataStore.getInstance();
        ArrayList<ShoppingItemInfo> shoppingInfoList =  listInstance.getList();
        
        // generate Hash
        String hashString = title;
        for (String str : recipeIngredientList) {
            hashString += str;
        }
        int hashCode = hashString.hashCode();

        // if nothing added so far, add fresh entry
        if (shoppingInfoList.size() == 0) {
            ArrayList<String> ingredientList = new ArrayList<>();
            ingredientList.add(ingredientItem);
            ShoppingItemInfo shoppingListInfo = new ShoppingItemInfo(title,
                    ingredientList, hashCode);
            shoppingInfoList.add(shoppingListInfo);
            return;
        }

        // existance check
        ShoppingItemInfo foundShopping = null;
        for (ShoppingItemInfo listItems : shoppingInfoList) {
            if (listItems.getHashCode() == hashCode) {
                foundShopping = listItems;
                break;
            }
        }


        if (foundShopping == null) {
            // recipe not found, add new one
            ArrayList<String> ingredientList = new ArrayList<>();
            ingredientList.add(ingredientItem);
            ShoppingItemInfo shoppingListInfo = new ShoppingItemInfo(title,
                    ingredientList, hashCode);
            shoppingInfoList.add(shoppingListInfo);
            return;
        } else {
            // recipe found, append to the list
            List<String> ingList = foundShopping.getRecipeContentList();
            ingList.add(ingredientItem);
        }
        
    }

    public static class ShoppingItemInfo {
        String recipeName;
        List<String> recipeContentList = new ArrayList<>();
        int hashCode;

        public String getRecipeName() {
            return recipeName;
        }

        public List<String> getRecipeContentList() {
            return recipeContentList;
        }

        public ShoppingItemInfo(String recipeName, List<String> shoppingIngredient, int hCode) {
            this.recipeName = recipeName;
            this.recipeContentList = shoppingIngredient;
            this.hashCode = hCode;
        }

        public int getHashCode() {
            return hashCode;
        }
    }


}
