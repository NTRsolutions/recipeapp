package com.example.recipe.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recipe.R;
import com.example.recipe.data.ShoppingListDataStore;
import com.example.recipe.utility.Config;

import java.util.List;

/**
 * Created by rajnish on 22/9/15.
 */
public class ShoppingListFragment extends Fragment {
    View rootView;
    ShoppingListDataStore mShoppingListDataStore;

    public ShoppingListFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mShoppingListDataStore = (ShoppingListDataStore) getArguments()
                .getSerializable(ShoppingListDataStore.SHOPPING_IST_DATA_STORE_KEY);

        rootView =  inflater.inflate(R.layout.fragment_shoppinglist, null, false);
        interceptTouchEvent(rootView);

        LinearLayout llayout = (LinearLayout) rootView.findViewById(R.id.shoppingingredient_list);
        List<ShoppingListDataStore.ShoppingItemInfo> shoppingList=  mShoppingListDataStore.getList();

        for(ShoppingListDataStore.ShoppingItemInfo info: shoppingList){

            TextView tvRecipeName = new TextView(getActivity());
            tvRecipeName.setText(info.getRecipeName());
            tvRecipeName.setTextSize(20);
            tvRecipeName.setTypeface(null, Typeface.BOLD);
            llayout.addView(tvRecipeName);
            List<String> content  = info.getRecipeContentList();
            for(String item : content) {
                TextView tvRecipeContent = new TextView(getActivity());
                tvRecipeContent.setText(item);
                tvRecipeContent.setTextSize(Config.TEXT_SIZE_CONTENT);
                llayout.addView(tvRecipeContent);
            }
            View v = new View(getActivity());
            v.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    2
            ));
            v.setBackgroundColor(Color.parseColor("#B3B3B3"));
            llayout.addView(v);

        }
        return rootView;

     }

    public void interceptTouchEvent(View rootView) {
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

}
