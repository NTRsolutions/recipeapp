package com.foodie.recipe.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodie.recipe.MainActivity;
import com.foodie.recipe.R;
import com.foodie.recipe.data.AnalyticsHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter MyAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isCategoryList = true;
    private MainActivity mMainActivity;
    public CategoryFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            mMainActivity = (MainActivity) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AnalyticsHandler.getInstance(getActivity()).sendScreenName(this.getClass().getSimpleName());
        View rootView = inflater.inflate(R.layout.fragment_category, container,
                false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        CategoryAdapter mAdapter = new CategoryAdapter(getActivity(),
                new CategoryAdapterListenerImpl());
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public class CategoryAdapterListenerImpl implements CategoryAdapter.CategoryAdapterListener {
        @Override
        public void onCategoryAdapterListener() {
            final RecipeAdapter adapter = new RecipeAdapter(
                    getActivity(), new RecipeAdapterListenerImpl());
            mRecyclerView.setAdapter(adapter);
            isCategoryList = false;
            return;
        }
    }

    private class RecipeAdapterListenerImpl implements RecipeAdapter.RecipeAdapterListener {

        @Override
        public void onRecipeAdapterListener(int recipeInfoId) {
            mMainActivity.showDetailView(recipeInfoId);
        }
    }

    public boolean onBackPressed() {
        if (!isCategoryList) {
            CategoryAdapter mAdapter = new CategoryAdapter(
                    getActivity(), new CategoryAdapterListenerImpl());
            mRecyclerView.setAdapter(mAdapter);
            isCategoryList = true;
            return true;
        }
        return false;
    }
}
