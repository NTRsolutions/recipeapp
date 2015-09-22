package com.example.recipe.ui;

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
        List<ShoppingListDataStore.ShoppingListInfo> shoppingList=  mShoppingListDataStore.getList();

        for(ShoppingListDataStore.ShoppingListInfo info: shoppingList){
            TextView tvRecipeName = new TextView(getActivity());
            tvRecipeName.setText(info.getRecipeName());
            llayout.addView(tvRecipeName);
            String[] content  = info.getRecipeContentList();
            for(String item : content){
                TextView tvRecipeContent = new TextView(getActivity());
                tvRecipeContent.setText(item);
                llayout.addView(tvRecipeContent);
            }

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
