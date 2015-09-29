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
public class BrowseFragment extends Fragment {
    public static final String SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY";

    public BrowseFragment() {
        // Required empty public constructor
    }

    private RecyclerView mRecyclerView;
    private MainActivity mMainActivity;
    private RecipeAdapter mAdapter;

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
        String query = getArguments().getString(SEARCH_QUERY_KEY, null);
        View rootView = inflater.inflate(R.layout.fragment_browse, container,
                false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecipeAdapter(getActivity(), new RecipeAdapterListenerImpl());
        mRecyclerView.setAdapter(mAdapter);

        RecipeDataStore.RecipeCategoryType  recipeCategoryType = RecipeDataStore.RecipeCategoryType.CATEGORY;
        RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                recipeCategoryType, new RecipeDataStoreListenerImpl(), query);

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
        public void onRecipeAdapterListener(RecipeInfo recipeDescription) {
            if (mMainActivity != null) {
                mMainActivity.showDetailView(recipeDescription);
            }
        }
    }

}
