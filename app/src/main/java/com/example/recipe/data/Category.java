package com.example.recipe.data;

import com.parse.ParseObject;

import java.security.SecureRandom;

/**
 * Created by root on 6/9/15.
 */
public class Category {
    int id;
    String category;
    String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Category getCategory(ParseObject parseObject) {
        Category category = new Category();
        category.id = parseObject.getInt("category_id");
        category.category = parseObject.getString("category");
        category.url = parseObject.getString("url");
        return category;
    }
}
