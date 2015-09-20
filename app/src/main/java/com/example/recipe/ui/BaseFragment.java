package com.example.recipe.ui;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.recipe.MainActivityListener;
import com.example.recipe.data.RecipeDescription;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {
    MainActivityListener activityListener;
    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivityListener) {
            activityListener = (MainActivityListener) activity;
        }
    }

}
