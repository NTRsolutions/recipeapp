package com.example.recipe.data;

import com.parse.ParseObject;

/**
 * Created by root on 6/9/15.
 */
public class Category {
    int id;
    String category;
    String url;
    String categoryType;
    int priority;
    String metaData;

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

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public static Category getCategory(ParseObject parseObject) {
        Category category = new Category();
        category.id = parseObject.getInt("category_id");
        category.category = parseObject.getString("category");
        category.url = parseObject.getString("url");
        category.priority = parseObject.getInt("priority");
        category.categoryType = parseObject.getString("category_type");
        category.metaData = parseObject.getString("meta_data");
        return category;
    }
}
