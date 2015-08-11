package com.example.recipe;

import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public interface MainActivityListener {
   List<DataStore.DataItem> getData();
   void showDetailView(String s);
}
