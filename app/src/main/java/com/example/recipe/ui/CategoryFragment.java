package com.example.recipe.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.data.CategoryDataStore;
import com.example.recipe.MainActivityListener;
import com.example.recipe.R;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter MyAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isCategoryList = true;

    public CategoryFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container,
                false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        CategoryAdapter mAdapter = new CategoryAdapter(getActivity(), new AdapterClickResolver());
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public class AdapterClickResolver implements AdapterListener {
        public String onAdapterClickListener(String s) {
            final RecipeAdapter adapter = new RecipeAdapter(
                    getActivity(), new BaseFragment.AdapterClickResolver());
            mRecyclerView.setAdapter(adapter);
            isCategoryList = false;
            return null;
        }
    }

    public boolean onBackPressed() {
        if (!isCategoryList) {
            CategoryAdapter mAdapter = new CategoryAdapter(getActivity(), new AdapterClickResolver());
            mRecyclerView.setAdapter(mAdapter);
            isCategoryList = true;
            return true;
        }
        return false;
    }
}
