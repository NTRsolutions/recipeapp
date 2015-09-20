package com.example.recipe.utility;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by rajnish on 10/8/15.
 */
public class Util {

    public static String readDataFromFile(String fileName) {
        File file = new File(fileName);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public static String jsonData = "{\"title\":\" Fattoush salad \",\"description\":\" Lots of chopped fresh herbs give this quick and simple Middle Eastern salad its pronounced flavour, and bite-sized pieces of crisply grilled pitta bread provide texture. Serve for a light meal with a hint of summer at any time of the year. \",\"ingredients\":[\"2 large white or sesame seed pitta breads\",\"4 tbsp olive oil\",\"juice of 1 lemon\",\"2 tbsp chopped fresh coriander\",\"2 tbsp chopped fresh mint, plus mint leaves, to garnish (optional)\",\"1/2 cucumber, cut into 1cm dice\",\"4 large tomatoes, quartered, cored and chopped\",\"4 spring onions, thinly sliced on the diagonal\",\"400g can black-eyed beans or chickpeas, drained and rinsed\"],\"directions\":[\"1. Toast the pitta breads: Warm the pitta breads in the toaster for 1 minute to make them easier to open up, then split each in half using a knife. Toast the four halves for 1 minute or until crisp and lightly browned. Tear into bite-sized pieces.\",\"2. Make the dressing: Put the olive oil and lemon juice into a small jug and whisk together with a fork. Season and stir in the fresh herbs.\",\"3. Assemble the salad: Put the cucumber, tomatoes, spring onions and beans or chickpeas in a bowl. Drizzle over the dressing, then toss together until well mixed. Immediately before serving, add the pitta bread pieces and mix again. Garnish with mint leaves, if you like.\"],\"nutritionList\":[\"Calories 321 kcal\",\"protein 10 g\",\"Fat 14 g\",\"Saturates 2 g\",\"Carbohydrates 41 g\"],\"serves\":\"serves: 4\",\"preparationTime\":\"Ready in  17 mins\"}";


}
