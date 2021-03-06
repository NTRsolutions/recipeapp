package com.foodie.recipe.data;

import com.foodie.recipe.utility.Config;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 22/9/15.
 */
public class ShoppingListDataStore implements Serializable {
    public static final String SHOPPING_IST_DATA_STORE_KEY = "SHOPPING_IST_DATASTORE__KEY";
    public static final String SAVED_SHOPPING_LIST = "SAVED_SHOPPING_LIST";
    ArrayList<ShoppingItemInfo> list = new ArrayList<>();

    private static ShoppingListDataStore sInstance;

    private ShoppingListDataStore() {
    }

    public static ShoppingListDataStore getInstance() {
        if (sInstance == null) {
            sInstance = new ShoppingListDataStore();
        }
        return sInstance;
    }

    public static void createFromJson(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return;
        }

        Gson gson = new Gson();
        sInstance = gson.fromJson(jsonString, ShoppingListDataStore.class);
    }

    public ArrayList<ShoppingItemInfo> getList() {
        return list;
    }

    public void setList(ArrayList<ShoppingItemInfo> list) {
        this.list = list;
    }

    public static boolean updateShoppingList(RecipeInfo recipeInfo, String ingredientItem) {
        AnalyticsHandler.getInstance(Config.APPLICATION_CONTEXT).logAppEvent(
                AnalyticsHandler.CATEGORY_SHOPPING_LIST_STR, "item_added");
        String title = recipeInfo.getTitle();
        List<String> recipeIngredientList = recipeInfo.getIngredients();
        ShoppingListDataStore listInstance = ShoppingListDataStore.getInstance();
        ArrayList<ShoppingItemInfo> shoppingInfoList = listInstance.getList();
        Boolean itemAdded = false;
        // generate Hash
        int hashCode = generateHashcode(recipeInfo);

        // if nothing added so far, add fresh entry
        if (shoppingInfoList.size() == 0) {
            ArrayList<String> ingredientList = new ArrayList<>();
            ingredientList.add(ingredientItem);
            ShoppingItemInfo shoppingListInfo = new ShoppingItemInfo(title,
                    ingredientList, hashCode, recipeInfo);
            shoppingInfoList.add(shoppingListInfo);
            itemAdded = true;
            return itemAdded;
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
                    ingredientList, hashCode, recipeInfo);
            shoppingInfoList.add(shoppingListInfo);
            itemAdded = true;
            return itemAdded;
        } else {
            // recipe found, append to the list
            List<String> ingList = foundShopping.getRecipeContentList();
            if (!ingList.contains(ingredientItem)) {
                ingList.add(ingredientItem);
                itemAdded = true;
            }
        }
        return itemAdded;
    }

    public static boolean deleteShoppingIngredientItem(
            RecipeInfo recipeInfo, String ingredientItem) {
        AnalyticsHandler.getInstance(Config.APPLICATION_CONTEXT).logAppEvent(
                AnalyticsHandler.CATEGORY_SHOPPING_LIST_STR, "item_deleted");
        Boolean removeItem = false;

        ShoppingListDataStore listInstance = ShoppingListDataStore.getInstance();
        ArrayList<ShoppingItemInfo> shoppingInfoList = listInstance.getList();

        // generate Hash
        int hashCode = generateHashcode(recipeInfo);


        // existance check
        ShoppingItemInfo foundShopping = null;
        for (ShoppingItemInfo listItems : shoppingInfoList) {
            if (listItems.getHashCode() == hashCode) {
                foundShopping = listItems;
                break;
            }
        }

        if(foundShopping == null){
          return false;
        }

        List<String> ingList = foundShopping.getRecipeContentList();
        if (ingList.contains(ingredientItem)) {
            ingList.remove(ingredientItem);
            removeItem = true;
        }

        return removeItem;
    }

    public static void deleteShoppingInfo(ShoppingItemInfo info){
        ShoppingListDataStore listInstance = ShoppingListDataStore.getInstance();
        ArrayList<ShoppingItemInfo> shoppingInfoList = listInstance.getList();
        shoppingInfoList.remove(info);

    }


    public static int generateHashcode(RecipeInfo recipeInfo) {
        String title = recipeInfo.getTitle();
        List<String> recipeIngredientList = recipeInfo.getIngredients();
        String hashString = title;
        for (String str : recipeIngredientList) {
            hashString += str;
        }
        int hashCode = hashString.hashCode();
        return hashCode;
    }

    public static boolean checkIfItemPresent(RecipeInfo recipeInfo,String ingredientItem){
        Boolean itemIsPresent = false;
        ShoppingListDataStore listInstance = ShoppingListDataStore.getInstance();
        ArrayList<ShoppingItemInfo> shoppingInfoList = listInstance.getList();

        // generate Hash
        int hashCode = generateHashcode(recipeInfo);


        // existance check
        ShoppingItemInfo foundShopping = null;
        for (ShoppingItemInfo listItems : shoppingInfoList) {
            if (listItems.getHashCode() == hashCode) {
                foundShopping = listItems;
                break;
            }
        }

        if(foundShopping == null){
            return false;
        }

        List<String> ingList = foundShopping.getRecipeContentList();
        if (ingList.contains(ingredientItem)) {
            itemIsPresent = true;
        }
        return itemIsPresent;
    }

    public String getJsonStr() {
        Gson gson = new Gson();
        String jsonShoppingStore = gson.toJson(this, ShoppingListDataStore.class);
        return jsonShoppingStore;
    }


    public static class ShoppingItemInfo implements Serializable{
        RecipeInfo recipeInfo;
        String recipeName;
        List<String> recipeContentList = new ArrayList<>();
        int hashCode;

        public String getRecipeName() {
            return recipeName;
        }

        public List<String> getRecipeContentList() {
            return recipeContentList;
        }

        public RecipeInfo getRecipeInfo() {
            return recipeInfo;
        }



        public ShoppingItemInfo(String recipeName, List<String> shoppingIngredient, int hCode
                ,RecipeInfo recipeInfo) {
            this.recipeInfo = recipeInfo;
            this.recipeName = recipeName;
            this.recipeContentList = shoppingIngredient;
            this.hashCode = hCode;
        }

        public int getHashCode() {
            return hashCode;
        }
    }


}
