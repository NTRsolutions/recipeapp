package com.example.recipe.data;

import android.content.Context;
import android.util.Log;

import com.example.recipe.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class CategoryDataStore {
    public static final String TAG = "CategoryDataStore";
    static List<Category> sCategoryList;

    public interface CategoryDataStoreListener {
       void onDataFetchComplete(List<Category> list);
    }

    public static void fetchAllCategoryData(Context cntx, final CategoryDataStoreListener listener) {

        if (sCategoryList == null) {
            sCategoryList = new ArrayList<>();
        }

        if (listener != null && sCategoryList.size() >0) {
            listener.onDataFetchComplete(sCategoryList);
            return;
        }

        ArrayList<String> list = RecipeDataStore.getsInstance(cntx).getRelatedTag();
        for (String tags : list) {
            Category category = new Category();
            category.setCategory(tags);
            category.setUrl("https://i.ytimg.com/vi/d2teJWH6QQ4/hqdefault.jpg");
            sCategoryList.add(category);
        }

        listener.onDataFetchComplete(sCategoryList);

//        ParseQuery<ParseObject> category = ParseQuery.getQuery(Category.class.getSimpleName());
//        category.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> results, ParseException e) {
//                List<Category> list = new ArrayList<>();
//                for (ParseObject object : results) {
//                    Category category = Category.getCategory(object);
//                    Log.d(TAG, "got result ");
//                    list.add(category);
//                }
//                if (listener != null) {
//                    sCategoryList = list;
//                    listener.onDataFetchComplete(list);
//                }
//            }
//        });
    }
}
