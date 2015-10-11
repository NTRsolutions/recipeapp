package com.example.recipe.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.MainActivity;
import com.example.recipe.R;
import com.example.recipe.data.RecipeDataStore;
import com.example.recipe.data.RecipeInfo;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeListFragment extends Fragment {

    private static final String EXTRA_RECIPE_LIST_TYPE = "EXTRA_RECIPE_LIST_TYPE";
    private RecyclerView mRecyclerView;
    private MainActivity mMainActivity;
    private MainActivity.Pages mPage;
    RecipeAdapter mAdapter;

    public static RecipeListFragment getInstance(MainActivity.Pages currentPage) {
        RecipeListFragment recipeListFragment = new RecipeListFragment();
        Bundle b = new Bundle();
        b.putSerializable(EXTRA_RECIPE_LIST_TYPE, currentPage);
        recipeListFragment.setArguments(b);
        return recipeListFragment;
    }

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
        mPage = (MainActivity.Pages) getArguments().getSerializable(EXTRA_RECIPE_LIST_TYPE);
        View rootView = inflater.inflate(R.layout.fragment_feed, container,
                false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecipeAdapter(getActivity(), new RecipeAdapterListenerImpl());
        mRecyclerView.setAdapter(mAdapter);

        RecipeDataStore.RecipeCategoryType recipeCategoryType = null;
        switch (mPage) {
            case FEED:
                recipeCategoryType =  RecipeDataStore.RecipeCategoryType.FEED;
                break;
            case RECENT:
                recipeCategoryType =  RecipeDataStore.RecipeCategoryType.HISTORY;
                break;
            case FAVOURITE:
                recipeCategoryType =  RecipeDataStore.RecipeCategoryType.FAVOURITE;
                break;
            case CATEGORIES:
                recipeCategoryType =  RecipeDataStore.RecipeCategoryType.CATEGORY;
                break;
        }

        RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                recipeCategoryType, new RecipeDataStoreListenerImpl(), null);
        return rootView;
    }

    private class RecipeDataStoreListenerImpl implements
            RecipeDataStore.RecipeDataStoreListener {

        @Override
        public void onDataFetchComplete(List<RecipeInfo> list) {
            mAdapter.updateDataList(list);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDataUpdate(List<RecipeInfo> list) {
            mAdapter.updateDataList(list);
            mAdapter.notifyDataSetChanged();
        }
    }


    private class RecipeAdapterListenerImpl implements RecipeAdapter.RecipeAdapterListener {

        @Override
        public void onRecipeAdapterListener(RecipeInfo recipeInfo) {
            if (mMainActivity != null) {
                mMainActivity.showDetailView(recipeInfo);
            }
        }
    }

}
