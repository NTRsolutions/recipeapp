package com.example.recipe.ui;


import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.recipe.data.RecipeDescription;
import com.example.recipe.utility.Config;
import com.example.recipe.R;
import com.example.recipe.utility.Util;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailFragment extends Fragment {
    public static final String RECIPE_DETAIL_KEY = "RECIPE_DETAIL_KEY";
    View rootView;
    public static float MAX_CARD_HEIGHT_PECENTAGE = 0.30f;
    RecipeDescription mRecipeDescritopn;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_recipe2, null, false);

        RelativeLayout banner = (RelativeLayout) rootView.findViewById(R.id.banner);
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * MAX_CARD_HEIGHT_PECENTAGE);
        banner.setLayoutParams(layoutParams);
        banner.requestLayout();


        mRecipeDescritopn = (RecipeDescription) getArguments().getSerializable(RECIPE_DETAIL_KEY);

        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.ingredient_list);
        List<String> list = mRecipeDescritopn.getIngredients();
        for (String ingredient : list) {
            TextView tv = new TextView(getActivity());
            tv.setText(ingredient);
            linearLayout.addView(tv);
        }

        TextView recipe = (TextView)rootView.findViewById(R.id.recipeId);
        recipe.setText(mRecipeDescritopn.getTitle());

        LinearLayout linearLayoutDirection = (LinearLayout) rootView.findViewById(R.id.direction_list);
        List<String> listDirection = mRecipeDescritopn.getDirections();
        for(String direction : listDirection){
            TextView tvDirection = new TextView(getActivity());
            tvDirection.setText(direction);
            linearLayoutDirection.addView(tvDirection);
        }
        TextView servesTitle = (TextView) rootView.findViewById(R.id.serves_title);
        servesTitle.setText("Serves");

        TextView serves = (TextView) rootView.findViewById(R.id.serves);
        serves.setText(mRecipeDescritopn.getServes());

        TextView preparationTitle = (TextView) rootView.findViewById(R.id.preparation_title);
        preparationTitle.setText("Preparation Time");
        TextView preparationTime = (TextView) rootView.findViewById(R.id.preparationtime);
        preparationTime.setText(mRecipeDescritopn.getPreparationTime());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
