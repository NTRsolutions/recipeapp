package com.foodie.recipe.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodie.recipe.R;
import com.foodie.recipe.data.AnalyticsHandler;
import com.foodie.recipe.data.RecipeInfo;
import com.foodie.recipe.data.ShoppingListDataStore;

import java.util.List;

/**
 * Created by rajnish on 22/9/15.
 */
public class ShoppingListFragment extends Fragment {
    public static final String TAG = "ShoppingListFragment";
    View rootView;
    LinearLayout shoppingListRootView;
    ShoppingListDataStore mShoppingListDataStore;
    public ShoppingListFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AnalyticsHandler.getInstance(getActivity()).sendScreenName(this.getClass().getSimpleName());
        mShoppingListDataStore = (ShoppingListDataStore) getArguments()
                .getSerializable(ShoppingListDataStore.SHOPPING_IST_DATA_STORE_KEY);

        rootView =  inflater.inflate(R.layout.fragment_shoppinglist, null, false);
        shoppingListRootView = (LinearLayout) rootView.findViewById(R.id.shoppingingredient_list);
        refreshShoppingListUi();
        setUpHeader();
        interceptTouchEvent(rootView);
        return rootView;

     }

    private void setUpHeader() {
        ImageView backButton = (ImageView) rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(
                        ShoppingListFragment.this).commit();
            }
        });
    }
    private void refreshShoppingListUi() {
        shoppingListRootView.removeAllViews();
        List<ShoppingListDataStore.ShoppingItemInfo> shoppingList=  mShoppingListDataStore.getList();

        for(ShoppingListDataStore.ShoppingItemInfo info: shoppingList){
            LinearLayout shoppingListItem = new LinearLayout(getActivity());
            shoppingListItem.setOrientation(LinearLayout.VERTICAL);
            Log.d(TAG, "CReating Shoopping List item : " + info.getRecipeName());

            TextView tvRecipeName = new TextView(getActivity());
            tvRecipeName.setText(info.getRecipeName());
            tvRecipeName.setTextSize(20);
            tvRecipeName.setTypeface(null, Typeface.BOLD);
            shoppingListItem.addView(tvRecipeName);
            List<String> content  = info.getRecipeContentList();
            for(String item : content) {
                TextView tvRecipeContent = new TextView(getActivity());
                tvRecipeContent.setText(item);
                tvRecipeContent.setTextSize(15);
                shoppingListItem.addView(tvRecipeContent);
            }
            View v = new View(getActivity());
            v.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    2
            ));
            v.setBackgroundColor(Color.parseColor("#B3B3B3"));
            shoppingListItem.addView(v);

            shoppingListRootView.addView(shoppingListItem);
            shoppingListItem.setOnClickListener(new ShoppingListLayoutListener(this, info));
        }
    }

    private void interceptTouchEvent(View rootView) {
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private static class ShoppingListLayoutListener implements View.OnClickListener {
        ShoppingListFragment listFragment;
        ShoppingListDataStore.ShoppingItemInfo mInfoShoppingList;
        Dialog mDialog;

        ShoppingListLayoutListener(ShoppingListFragment frg,
                                         ShoppingListDataStore.ShoppingItemInfo info) {
            listFragment = frg;
            mInfoShoppingList = info;
            Log.d(TAG, "ShoppingListLayoutListener : " + info.getRecipeName());
        }

        @Override
        public void onClick(View v) {
            mDialog = new Dialog(listFragment.getActivity());
            mDialog.setContentView(R.layout.shopping_dialog);
            mDialog.setTitle(mInfoShoppingList.getRecipeName());

            TextView deleteAllIngredient = (TextView) mDialog.findViewById(R.id.deleteall);
            deleteAllIngredient.setText("Delete Recipe Ingredients");
            deleteAllIngredient.setOnClickListener(
                    new DeleteAllIngredientListListerner(listFragment, mDialog, mInfoShoppingList));

            TextView share = (TextView) mDialog.findViewById(R.id.sharelist);
            share.setText("Share list");

            TextView viewRecipe = (TextView) mDialog.findViewById(R.id.viewrecipe);
            viewRecipe.setText("View Recipe Detail");
            viewRecipe.setOnClickListener(new viewRecipeListener(listFragment, mDialog, mInfoShoppingList));

            mDialog.show();
        }
    }

    private static class DeleteAllIngredientListListerner implements View.OnClickListener {
        ShoppingListFragment listFragment;
        ShoppingListDataStore.ShoppingItemInfo mInfoDeleteIngredient;
        Dialog mDialog;

        DeleteAllIngredientListListerner(ShoppingListFragment frg, Dialog dialog,
                                         ShoppingListDataStore.ShoppingItemInfo info) {
            listFragment = frg;
            mInfoDeleteIngredient = info;
            mDialog = dialog;
        }

        @Override
        public void onClick(View v) {
            ShoppingListDataStore.deleteShoppingInfo(mInfoDeleteIngredient);
            listFragment.refreshShoppingListUi();
            mDialog.dismiss();
        }
    }

    private static class viewRecipeListener implements  View.OnClickListener{

        ShoppingListFragment listFragment;
        ShoppingListDataStore.ShoppingItemInfo mInfoViewRecipe;
        Dialog mDialog;

        public viewRecipeListener(ShoppingListFragment frg, Dialog dialog,
                                  ShoppingListDataStore.ShoppingItemInfo info) {

            listFragment = frg;
            mInfoViewRecipe = info;
            mDialog = dialog;
        }

        @Override
        public void onClick(View v) {
            RecipeInfo recipeInfo = mInfoViewRecipe.getRecipeInfo();
            Bundle bundle = new Bundle();
            bundle.putSerializable(RecipeDetailFragment.RECIPE_DETAIL_KEY, Integer.parseInt(recipeInfo.getDocId()));
            RecipeDetailFragment rFrag = new RecipeDetailFragment();
            rFrag.setArguments(bundle);

            listFragment.getFragmentManager().beginTransaction()
                    .replace(R.id.full_screen_view, rFrag, "ShoppingList Fragment")
                    .addToBackStack(null)
                    .commit();
            mDialog.dismiss();
        }
    }
}
