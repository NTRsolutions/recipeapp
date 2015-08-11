package com.example.recipe;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment {
    View rootView;

    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        String textRecipe = getArguments().getString("Recipe");
        rootView =  inflater.inflate(R.layout.fragment_recipe2, null, false);
        TextView recipe = (TextView)rootView.findViewById(R.id.recipeId);
        recipe.setText(textRecipe);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        String path = Environment.getExternalStorageDirectory() + Config.SEPERATOR +
                Config.APP_NAME + Config.SEPERATOR + "recipe_detail_1.txt";

        String data = Util.readDataFromFile(path);
        TextView recipe = (TextView)rootView.findViewById(R.id.recipeId);
        recipe.setText(data);


    }
}
