package com.example.recipe.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rajnish on 22/9/15.
 */
public class ShoppingListDataStore implements Serializable{
    public static final String SHOPPING_IST_DATA_STORE_KEY = "SHOPPING_IST_DATASTORE__KEY";
    List<ShoppingListInfo> list = new ArrayList<>();

    private static ShoppingListDataStore sInstance;

    private ShoppingListDataStore() {}

    public static ShoppingListDataStore getInstance() {
        if (sInstance == null) {
            sInstance = new ShoppingListDataStore();
            populateDummyShoppingListStore(sInstance);
        }
        return sInstance;
    }


    // dummy data
    static String[] recipeName = {"aalo paratha","paneer paratha","mutterpaneer","aalo bhindi"};
    HashMap<String, ArrayList<String>>  recipeToIngredientMap = new HashMap<>();



    private static ShoppingListDataStore populateDummyShoppingListStore(ShoppingListDataStore store) {
        ShoppingListInfo shoppingListInfo1 = new ShoppingListInfo(
                recipeName[0], new String[]{"Aaloo", "Wheat"});
        store.list.add(shoppingListInfo1);

        ShoppingListInfo shoppingListInfo2 = new ShoppingListInfo(
                recipeName[1], new String[]{"paneer", "Wheat"});
        store.list.add(shoppingListInfo2);

        ShoppingListInfo shoppingListInfo3 = new ShoppingListInfo(
                recipeName[2], new String[]{"mutter", "Wheat"});
        store.list.add(shoppingListInfo3);

        ShoppingListInfo shoppingListInfo4 = new ShoppingListInfo(
                recipeName[3], new String[]{"bhindi", "Wheat"});
        store.list.add(shoppingListInfo4);

        return store;
    }

    public List<ShoppingListInfo> getList() {
        return list;
    }

    public void setList(List<ShoppingListInfo> list) {
        this.list = list;
    }

    public static class ShoppingListInfo{
        String recipeName;
        String[] recipeContentList;

        public String getRecipeName() {
            return recipeName;
        }

        public void setRecipeName(String recipeName) {
            this.recipeName = recipeName;
        }

        public String[] getRecipeContentList() {
            return recipeContentList;
        }

        public void setRecipeContentList(String[] recipeContentList) {
            this.recipeContentList = recipeContentList;
        }

        public ShoppingListInfo(String recipeName,String[] shoppingIngredient) {
            this.recipeName = recipeName;
            this.recipeContentList = shoppingIngredient;
        }
    }


}
