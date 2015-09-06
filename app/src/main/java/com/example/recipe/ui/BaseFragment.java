package com.example.recipe.ui;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.recipe.MainActivityListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {
    MainActivityListener activityListener;

    public interface AdapterListener {
        String onAdapterClickListener(String s);
    }

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

    public class AdapterClickResolver implements AdapterListener {
        public String onAdapterClickListener(String s) {
            activityListener.showDetailView(s);
            Log.d("TAG", "in fragment click"+s);
            return s;
        }
    }
}
