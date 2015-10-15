package com.example.recipe.data;

/**
 * Created by root on 26/9/15.
 */
public class FoodCategoryList {

    public enum FoodCategory{
        NORTH_INDIAN("NorthIndian"),
        SOUTH_INDIAN("SouthIndian"),
        VEGETARIAN("Vegetarian"),
        NON_VEGETARIAN("NonVegetarian"),

        EASY("Easy"),
        HEALTHY("Healthy"),

        //Sweet's
        MILKSHAKES("MilkShakes"),
        CAKES("Cakes"),
        DESSERTS("Desserts"),
        KIDS("Kids"),
        DRINKS("Drinks"),
        BEVERAGE("beverage"),

        TANDOOR("Tandoor"),

        CHINESE("Chinese"),
        SOUP("Soup"),
        CHUTNEY("Chutney"),
        SANDWHICH("sandwich"),
        CURRY("Curry"),

        SNACKS("Snacks"),
        SAUCE("sauce"),
        THAI("Thai"),
        FRENCH("French"),
        ITALIAN("Italian"),
        SALAD("Salad"),
        PASTA("Pasta"),
        PARATHA("Paratha"),

        // food items
        CHICKEN("Chicken"),
        LAMB("Lamb"),
        MUTTON("Mutton"),
        EGG("Egg"),
        PANEER("Paneer"),
        PORK("Pork"),
        BEEF("Beef"),
        FISH("Fish"),
        PRAWN("Prawn"),

        BREAKFAST("Breakfast"),
        LUNCH("Lunch"),
        DINNER("Dinner"),

        //Carbohydrate
        RICE("Rice"),

        // location Based
        RAJASTHANI("rajasthani"),
        PUNJABI("punjabi"),
        GUJRATI("gujrati"),
        BENGALI("Bengali"),
        KERALA("Kerela"),

        BAKED("baked"),

        //Hidden Tags
        DEFAULT("default");

        String val;

        FoodCategory(String str) {
            val = str.toLowerCase();
        };

        public String getValue() {return val;}
    }
}
