package com.example.recipe.data;

import com.example.recipe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class CategoryDataStore {

    String[] CUPCAKES = new String[] { "Italian", "French", "Greek",
            "Indian"};

    String[] MIN_DESCRIPTION = new String[] { "strawberry with fresh cream topping", "chocolate muffin with choco chips", "delicious and healthy orange cake",
            "All time favourite"};

    String[] IMAGES = new String[]{
            "http://blog.mydala.com/wp-content/uploads/2015/07/88.jpg",
            "http://www.birminghamrestaurantsandbars.com/wp-content/uploads/2015/06/873b077b15bbec54aea9e0105b84879d.jpg",
            "http://cf2.foodista.com/sites/default/files/styles/featured/public/field/image/tz.jpg",
            "http://youqueen.com/wp-content/uploads/2015/03/Traditional-Indian-Food.jpg"};


    public List<CategoryDataItem> getAllData() {
        List<CategoryDataItem> list = new ArrayList<>();
        int position =CUPCAKES.length;
        for(int i=0;i<position;i++){
            String item = CUPCAKES[i];
            String des = MIN_DESCRIPTION[i];
            String images = IMAGES[i];
            CategoryDataItem dataItem = new CategoryDataItem(item, des, images);
            list.add(dataItem);
        }
        return list;
    }

    public static class CategoryDataItem {
        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getItemDescription() {
            return itemDescription;
        }

        public void setItemDescription(String itemDescription) {
            this.itemDescription = itemDescription;
        }

        public String getImageResource() {
            return imageResource;
        }

        public void setImageResource(String imageResource) {
            this.imageResource = imageResource;
        }

        String item;
        String itemDescription;
        String imageResource;

        CategoryDataItem(String itm, String dsc, String rsc){
            item = itm;
            itemDescription = dsc;
            imageResource = rsc;
        }
    }
}
