package com.foodie.recipe.data;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by rajnish on 6/8/15.
 */
public class CategoryDataStore {
    public static final String TAG = "CategoryDataStore";
    static List<Category> sCategoryList;

    public interface CategoryDataStoreListener {
       void onDataFetchComplete(List<Category> list);
    }

    public enum CategoryType {
        TAGS,
        TAGS_LIST,
        TAGS_DISTRIBUTION_LIST,
        TAGS_DOCID_LIST,
        UNRESOLVED;

        public String getValue() {
            String name = name().replace("_", "");
            return name.toLowerCase();
        }

        public static CategoryType getTypeFromString(String str) {
            for (CategoryType type : CategoryType.values()) {
                if (str.equalsIgnoreCase(type.getValue())) {
                    return type;
                }
            }
            return UNRESOLVED;
        }
    }

    public static void fetchAllCategoryData(Context cntx, final CategoryDataStoreListener listener) {

        if (sCategoryList == null) {
            sCategoryList = new ArrayList<>();
        }

        if (listener != null && sCategoryList.size() >0) {
            listener.onDataFetchComplete(sCategoryList);
            return;
        }

        ParseQuery<ParseObject> category = ParseQuery.getQuery(Category.class.getSimpleName());
        category.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException exception) {
                if (exception != null  || results == null) {
                    Log.d(TAG, exception.getCause().toString());
                    return;
                }

                List<Category> list = new ArrayList<>();
                TreeMap<Integer, Category> orderMap = new TreeMap<>();
                for (ParseObject object : results) {
                    Category category = Category.getCategory(object);
                    Log.d(TAG, "got result ");
                    if (category.priority > 0) {
                        orderMap.put(category.priority, category);
                    }
                }

                for (int index : orderMap.descendingKeySet()) {
                    Category category = orderMap.get(index);
                    list.add(category);
                }

                if (listener != null) {
                    sCategoryList = list;
                    listener.onDataFetchComplete(list);
                }
            }
        });

    }
}
