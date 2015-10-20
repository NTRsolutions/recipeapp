package com.example.recipe.data;

import android.content.Context;

import com.example.recipe.utility.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

/**
 * Created by rajnish on 21/10/15.
 */
public class FeedDataGenerator {

    private static FeedDataGenerator sInstance;
    Context mContext;
    private int MIN_SLICING_PER_WINDOW = 3;

    private FeedDataGenerator(Context context) {
        mContext = context;
    }

    public static FeedDataGenerator getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FeedDataGenerator(context);
        }
        return sInstance;
    }

    public List<RecipeInfo>  getFeedData(Context context) {
        TreeMap<String, Integer> map = UserInfo.getInstance(context).fetchDataForFeed();
        List<RecipeInfo> finalList = new ArrayList<>();
        TreeMap<Integer, Queue<RecipeInfo>> distribution = new TreeMap<>();
        int seedPoint =  Config.TOTAL_FEED_SEED_COUNT;;
        int index = 0 ;
        for (String tag : map.keySet()) {
            index ++;
            double decayFunction = seedPoint * Math.pow(0.5f, index);
            if (decayFunction <= 1) {
                break;
            }
            List<RecipeInfo>  infos = RecipeDataStore.getsInstance(context).searchDocuments(
                    tag, (int)decayFunction);

            distribution.put((int) decayFunction, new LinkedList<>(infos));
        }

        while (true) {
            boolean toBreak = true;
            List<RecipeInfo> tempList = new ArrayList<>();
            for(int key :distribution.descendingKeySet()) {
                Queue<RecipeInfo> list = distribution.get(key);
                int toRemoveSize = list.size() >= MIN_SLICING_PER_WINDOW ? MIN_SLICING_PER_WINDOW : list.size();

                for (int i = 0 ; i < toRemoveSize; i++) {
                    tempList.add(list.remove());
                    toBreak = false;
                }
            }

            if (tempList.size() > 0) {
                 Collections.shuffle(tempList);
                 finalList.addAll(tempList);
            }

            if (toBreak) {
                break;
            }
        }

        return finalList;
    }
}
