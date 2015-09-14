package com.example.recipe.ui;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.recipe.data.RecipeDescription;
import com.example.recipe.utility.Config;
import com.example.recipe.R;
import com.example.recipe.utility.Util;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailFragment extends Fragment {
    public static final String RECIPE_DETAIL_KEY = "RECIPE_DETAIL_KEY";
    View rootView;
    RecipeDescription mRecipeDescritopn;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRecipeDescritopn = (RecipeDescription) getArguments().getSerializable(RECIPE_DETAIL_KEY);
        rootView =  inflater.inflate(R.layout.fragment_recipe2, null, false);
        TextView recipe = (TextView)rootView.findViewById(R.id.recipeId);
        recipe.setText(mRecipeDescritopn.getTitle());
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
