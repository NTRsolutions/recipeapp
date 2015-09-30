package com.example.recipe.ui;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recipe.MainActivity;
import com.example.recipe.R;
import com.example.recipe.data.RecipeDataStore;
import com.example.recipe.data.RecipeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseFragment extends Fragment {
    public static final String SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY";
    public static final String SEARCH_TAG_ITEMS = "SEARCH_TAG_ITEMS";

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
        ArrayList<String> tagItems =  getArguments().getStringArrayList("SEARCH_TAG_ITEMS");

        final TextView queryText = (TextView) rootView.findViewById(R.id.tagText);
        queryText.setText(query);

        LinearLayout tagListLayout = (LinearLayout) rootView.findViewById(R.id.tag_list);
        for(final String singletag : tagItems){
            TextView tv = new TextView(getActivity());
            tv.setText(singletag);

            tv.setBackgroundResource(R.drawable.round_button_drawable);
            tv.setTextSize(16);
            tv.setPadding(10, 10, 10, 10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    tagListLayout.getLayoutParams());
            params.setMargins(5, 10, 5, 10);
            tagListLayout.addView(tv, params);

            tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    queryText.setText(singletag);
                    RecipeDataStore.RecipeCategoryType  recipeCategoryType = RecipeDataStore.RecipeCategoryType.CATEGORY;
                    RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                            recipeCategoryType, new RecipeDataStoreListenerImpl(), singletag);
                }
            });
        }

        ImageView clearTag = (ImageView) rootView.findViewById(R.id.cleartag);
        clearTag.setColorFilter(getResources().getColor(R.color.black));
        clearTag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

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
