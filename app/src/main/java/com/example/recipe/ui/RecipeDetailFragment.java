package com.example.recipe.ui;


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.recipe.data.RecipeInfo;
import com.example.recipe.data.ShoppingListDataStore;
import com.example.recipe.utility.Config;
import com.example.recipe.R;
import com.example.recipe.utility.SpringOnTouchListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailFragment extends Fragment {
    public static final String RECIPE_DETAIL_KEY = "RECIPE_DETAIL_KEY";
    View rootView;
    public static float MAX_CARD_HEIGHT_PECENTAGE = 0.35f;
    RecipeInfo mRecipeData;
    Boolean mItemUpdated;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_recipe2, null, false);
        mRecipeData = (RecipeInfo) getArguments()
                .getSerializable(RECIPE_DETAIL_KEY);
        ImageView recipeImage = (ImageView) rootView.findViewById(R.id.recipe_image);


        if (mRecipeData.getImageUrl() != null) {
            Picasso.with(recipeImage.getContext()).load(
                    mRecipeData.getImageUrl()).into(recipeImage);
        }
        setupShareRecipe(rootView);
        setUpFavouriteRecipe(rootView);
        setUpBannerSize(rootView);
        setUpTitle(rootView);
        setUpIngredientView(rootView);
        setUpDirection(rootView);
        setUpServesTxt(rootView);
        setUpPrepTimetxt(rootView);
        setUpNutritionView(rootView);
        return rootView;
    }

    public void setUpPrepTimetxt(View rootView) {
        TextView preparationTitle = (TextView) rootView.findViewById(R.id.preparation_title);
        preparationTitle.setText("Preparation Time");
        TextView preparationTime = (TextView) rootView.findViewById(R.id.preparationtime);
        preparationTime.setText(mRecipeData.getPreparationTime());
        preparationTime.setTextSize(Config.TEXT_SIZE_CONTENT);
    }

    public void setUpServesTxt(View rootView) {
        TextView servesTitle = (TextView) rootView.findViewById(R.id.serves_title);
        servesTitle.setText("Serves");
        TextView serves = (TextView) rootView.findViewById(R.id.serves);
        serves.setText(mRecipeData.getmServing());
        serves.setTextSize(Config.TEXT_SIZE_CONTENT);
    }

    public void setUpDirection(View rootView) {
        LinearLayout linearLayoutDirection = (LinearLayout) rootView
                .findViewById(R.id.direction_list);
        List<String> listDirection = mRecipeData.getDirections();
        for(String direction : listDirection){
            TextView tvDirection = new TextView(getActivity());
            tvDirection.setText(direction);
            tvDirection.setTextSize(Config.TEXT_SIZE_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    linearLayoutDirection.getLayoutParams());
            params.setMargins(10, 10, 10, 10);
            linearLayoutDirection.addView(tvDirection, params);
        }
    }

    public void setUpNutritionView(View rootView) {
        LinearLayout linearLayoutDirection = (LinearLayout) rootView
                .findViewById(R.id.nutrition_list);
        List<String> listNutrition = mRecipeData.getNutritionList();

        if (listNutrition == null) {
            return;
        }

        for(String direction : listNutrition){
            TextView tvNutrition = new TextView(getActivity());
            tvNutrition.setText(direction);
            tvNutrition.setTextSize(Config.TEXT_SIZE_CONTENT);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    linearLayoutDirection.getLayoutParams());
            params.setMargins(10, 10, 10, 10);
            linearLayoutDirection.addView(tvNutrition, params);
        }
    }

    public void setUpTitle(View rootView) {
        TextView recipe = (TextView)rootView.findViewById(R.id.recipeId);
        recipe.setText(mRecipeData.getTitle());
    }

    public void setUpBannerSize(View rootView) {
        RelativeLayout banner = (RelativeLayout) rootView.findViewById(R.id.banner);
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * MAX_CARD_HEIGHT_PECENTAGE);
        banner.setLayoutParams(layoutParams);
        banner.requestLayout();
    }

    public void setUpIngredientView(final View rootView){
        LinearLayout ingredientListLayout = (LinearLayout) rootView.findViewById(R.id.ingredient_list);
        List<String> list = mRecipeData.getIngredients();
        Resources resources = getResources();
        for (final String ingredient : list) {
            final LinearLayout innerLineaarLayout = new LinearLayout(getActivity());
            innerLineaarLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLineaarLayout.setGravity(Gravity.CENTER);

            final ImageView addImage = new ImageView(getActivity());
            addImage.setImageResource(R.drawable.addbutton);
            addImage.setColorFilter(resources.getColor(R.color.colorAccent));
            ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(50, 50);
            addImage.setLayoutParams(imageParams);
            addImage.setPadding(0, 3, 10, 3);
            if(ShoppingListDataStore.checkIfItemPresent(mRecipeData,ingredient)){
                addImage.setImageResource(R.drawable.checkbutton);
                innerLineaarLayout.addView(addImage);
                innerLineaarLayout.setSelected(true);
            }else {
                innerLineaarLayout.addView(addImage);
                innerLineaarLayout.setSelected(false);
            }

            TextView tv = new TextView(getActivity());
            tv.setText(ingredient);
            tv.setTextSize(Config.TEXT_SIZE_CONTENT);
            innerLineaarLayout.addView(tv);

            innerLineaarLayout.setOnClickListener(new View.OnClickListener() {
                FrameLayout parentView = (FrameLayout) rootView.findViewById(R.id.parentView);

                @Override
                public void onClick(View v) {
                    innerLineaarLayout.setSelected(!innerLineaarLayout.isSelected());
                    boolean isSelected = innerLineaarLayout.isSelected();
                    if (isSelected) {
                        mItemUpdated = ShoppingListDataStore.updateShoppingList(mRecipeData, ingredient);
                        Snackbar.make(parentView, "Item is added to shopping cart", Snackbar.LENGTH_LONG)
                                .setAction("show", null)
                                .show();
                        addImage.setImageResource(R.drawable.checkbutton);
                    } else {
                        mItemUpdated = ShoppingListDataStore.deleteShoppingIngredientItem(mRecipeData, ingredient);
                        Snackbar.make(parentView, "Item is removed from shopping cart", Snackbar.LENGTH_LONG)
                                .setAction("show", null)
                                .show();
                        addImage.setImageResource(R.drawable.addbutton);

                    }
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ingredientListLayout.getLayoutParams());
            params.setMargins(5, 9, 5, 9);
            ingredientListLayout.addView(innerLineaarLayout, params);
        }
    }

    public void setUpFavouriteRecipe(View rootView){
        final ImageView favouriteRecipe = (ImageView) rootView.findViewById(R.id.favourite);
        Resources res = rootView.getContext().getResources();
        final int selectedColor = res.getColor(R.color.colorAccent);
        final int unSelectedColor = res.getColor(R.color.grey);
        favouriteRecipe.setColorFilter(unSelectedColor);
        favouriteRecipe.setSelected(false);
        favouriteRecipe.setOnTouchListener(new SpringOnTouchListener(favouriteRecipe) {
            @Override
            protected void onClick(View view) {
                favouriteRecipe.setSelected(!favouriteRecipe.isSelected());
                if (favouriteRecipe.isSelected()) {
                    favouriteRecipe.setColorFilter(selectedColor);
                } else {
                    favouriteRecipe.setColorFilter(unSelectedColor);
                }
            }
        });
    }

    public void setupShareRecipe(View rootView){
        final ImageView shareRecipe = (ImageView) rootView.findViewById(R.id.shareicon);
        Resources res = rootView.getContext().getResources();
        final int selectedColor = res.getColor(R.color.colorAccent);
        shareRecipe.setColorFilter(selectedColor);
        shareRecipe.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                File file = new File(Environment.getExternalStorageDirectory(),
//                        "PicsArt_1402676920928.jpg");
                shareIntent.setType("image/*");
                String imagePath = Environment.getExternalStorageDirectory()
                        + "PicsArt_1402676920928.jpg";
//                Uri outputFileUri = Uri.parse(file.getAbsolutePath());
                File imageFileToShare = new File(imagePath);
                Uri uri = Uri.fromFile(imageFileToShare);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

               getActivity().startActivity(Intent.createChooser(shareIntent, "SEND"));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
