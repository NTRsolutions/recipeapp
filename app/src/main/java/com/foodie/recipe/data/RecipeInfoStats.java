package com.foodie.recipe.data;

/**
 * Created by rajnish on 22/10/15.
 */
public class RecipeInfoStats {
    String objectId;
    int recipeinfoId;
    int viewCount;
    int favourateCount;

    public enum Keys {
        RECIPEINFO_ID,
        TITLE,
        VIEW_COUNT,
        FAVOURATE_COUNT;

        public String getValue() {
            return this.name().toLowerCase();
        }
    }
}
