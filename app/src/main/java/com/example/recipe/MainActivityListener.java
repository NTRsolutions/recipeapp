package com.example.recipe;

import com.example.recipe.data.CategoryDataStore;

import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public interface MainActivityListener {
   List<CategoryDataStore.CategoryDataItem> getData();
   void showDetailView(String s);
}
