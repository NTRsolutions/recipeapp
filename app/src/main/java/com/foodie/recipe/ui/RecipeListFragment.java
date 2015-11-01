package com.foodie.recipe.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodie.recipe.MainActivity;
import com.foodie.recipe.R;
import com.foodie.recipe.data.AnalyticsHandler;
import com.foodie.recipe.data.RecipeDataStore;
import com.foodie.recipe.data.RecipeInfo;

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
        AnalyticsHandler.getInstance(getActivity()).sendScreenName(this.getClass().getSimpleName());
        mPage = (MainActivity.Pages) getArguments().getSerializable(EXTRA_RECIPE_LIST_TYPE);
        View rootView = inflater.inflate(R.layout.fragment_feed, container,
                false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        setUpHeader(rootView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecipeAdapter(getActivity(), new RecipeAdapterListenerImpl());
        mRecyclerView.setAdapter(mAdapter);

        RecipeDataStore.RecipeDataType recipeDataType = null;
        switch (mPage) {
            case FEED:
                recipeDataType =  RecipeDataStore.RecipeDataType.FEED;
                break;
            case RECENT:
                recipeDataType =  RecipeDataStore.RecipeDataType.HISTORY;
                break;
            case FAVOURITE:
                recipeDataType =  RecipeDataStore.RecipeDataType.FAVOURITE;
                break;
            case CATEGORIES:
                recipeDataType =  RecipeDataStore.RecipeDataType.TAGS;
                break;
        }

        RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                recipeDataType, new RecipeDataStoreListenerImpl(), null);
        return rootView;
    }

    void setUpHeader(View rootView) {
        RelativeLayout headerBar = (RelativeLayout) rootView.findViewById(R.id.headerBar);
        TextView title = (TextView) rootView.findViewById(R.id.title);
        ImageView backButton = (ImageView) rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(
                        RecipeListFragment.this).commit();
            }
        });

        switch (mPage) {
            case FEED:
            case CATEGORIES:
                headerBar.setVisibility(View.GONE);
                break;
            case RECENT:
                title.setText("History");
                break;
            case FAVOURITE:
                title.setText("Favourite");
                break;
        }

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
        public void onRecipeAdapterListener(int recipeInfoId) {
            if (mMainActivity != null) {
                mMainActivity.showDetailView(recipeInfoId);
            }
        }
    }

}
