package com.example.recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class DataStore {

    String[] CUPCAKES = new String[] { "Strawberry", "Chocolate", "Orange",
            "Vanilla"};

    String[] MIN_DESCRIPTION = new String[] { "strawberry with fresh cream topping", "chocolate muffin with choco chips", "delicious and healthy orange cake",
            "All time favourite"};

    Integer[] IMAGES = new Integer[]{R.drawable.strawberry, R.drawable.chocolate, R.drawable.orange, R.drawable.vanilla};


    public List<DataItem> getAllData() {
        List<DataItem> list = new ArrayList<>();
        int position =CUPCAKES.length;
        for(int i=0;i<position;i++){
            String item = CUPCAKES[i];
            String des = MIN_DESCRIPTION[i];
            Integer images = IMAGES[i];
            DataItem dataItem = new DataItem(item, des, images);
            list.add(dataItem);
        }
        return list;
    }

    public static class DataItem {
        String item;
        String itemDescription;
        Integer imageResource;

        DataItem(String itm, String dsc, Integer rsc){
            item = itm;
            itemDescription = dsc;
            imageResource = rsc;
        }
    }
}
