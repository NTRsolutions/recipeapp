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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recipe.MainActivity;
import com.example.recipe.R;
import com.example.recipe.data.RecipeDataStore;
import com.example.recipe.data.RecipeDataStore.RecipeDataType;
import com.example.recipe.data.RecipeInfo;
import com.example.recipe.data.graph.Graph;

import java.util.ArrayList;
import java.util.List;

import static com.example.recipe.data.CategoryDataStore.*;
import static com.example.recipe.data.RecipeDataStore.RecipeDataType.TAGS;
import static com.example.recipe.data.RecipeDataStore.RecipeDataType.TAGS_DOCID_LIST;
import static com.example.recipe.data.RecipeDataStore.RecipeDataType.TAGS_LIST;
import static com.example.recipe.data.RecipeDataStore.RecipeDataType.TAGS_PROBABILITY_LIST;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseFragment extends Fragment {
    public static final String SOURCE_KEY = "SOURCE_KEY";
    public static final String ORIGIN_FROM_TAGS = "ORIGIN_FROM_TAGS";
    public static final String ORIGIN_FROM_CATEGORY = "ORIGIN_FROM_CATEGORY";
    public static final String SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY";
    public static final String CATEGORY_TYPE_KEY = "CATEGORY_TYPE_KEY";
    public static final String CATEGORY_METADATA_KEY = "CATEGORY_METADATA_KEY";
    public BrowseFragment() {
        // Required empty public constructor
    }

    private RecyclerView mRecyclerView;
    private MainActivity mMainActivity;
    private RecipeAdapter mAdapter;
    private View rootView;

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

        rootView = inflater.inflate(R.layout.fragment_browse, container,
                false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecipeAdapter(getActivity(), new RecipeAdapterListenerImpl());
        mRecyclerView.setAdapter(mAdapter);
        
        
        String originType  = getArguments().getString(SOURCE_KEY, null);
        switch (originType) {
            case ORIGIN_FROM_TAGS:
                String query = getArguments().getString(SEARCH_QUERY_KEY, null);
                handleTagOriginDataPopulation(query, true);
                break;
            case ORIGIN_FROM_CATEGORY:
                rootView.findViewById(R.id.card_view).setVisibility(View.GONE);
                rootView.findViewById(R.id.card_view_button).setVisibility(View.GONE);
                handleCategoryOriginDataPopulation();
                break;
        }

        return rootView;
    }

    private void handleCategoryOriginDataPopulation() {
        String categoryType = getArguments().getString(CATEGORY_TYPE_KEY, null);
        String metaData = getArguments().getString(CATEGORY_METADATA_KEY, null);

        if (metaData == null || metaData.equalsIgnoreCase("")) {
            return;
        }

        RecipeDataType recipeDataType = null;
        CategoryType type = CategoryType.getTypeFromString(categoryType);
        switch (type) {
            case TAGS:
                handleTagOriginDataPopulation(metaData, false);
                break;
            case TAGS_LIST:
                recipeDataType = TAGS_LIST;
                RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                        recipeDataType, new RecipeDataStoreListenerImpl(), metaData);
                break;
            case TAGS_DISTRIBUTION_LIST:
                recipeDataType = TAGS_PROBABILITY_LIST;
                RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                        recipeDataType, new RecipeDataStoreListenerImpl(), metaData);
                break;
            case TAGS_DOCID_LIST:
                recipeDataType = TAGS_DOCID_LIST;
                RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                        recipeDataType, new RecipeDataStoreListenerImpl(), metaData);
                break;
            case UNRESOLVED:
                // DO Nothing
                Log.d(TAG, "Category Type UNRESOLVED");
                break;

        }

    }

    private void handleTagOriginDataPopulation(String query, boolean showRelated) {
        if (showRelated) {
            populateRelatedView(query);
        }

        RecipeDataType recipeDataType = TAGS;
        RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                recipeDataType, new RecipeDataStoreListenerImpl(), query);
    }

    private void populateRelatedView(String query) {
        ArrayList<String> tagItems =  Graph.getsInstance().findRelated(query);
        final TextView queryText = (TextView) rootView.findViewById(R.id.tagText);
        queryText.setText(query);

        LinearLayout tagListLayout = (LinearLayout) rootView.findViewById(R.id.tag_list);
        tagListLayout.removeAllViews();
        for(final String singletag : tagItems){
            final TextView tv = new TextView(getActivity());
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
                    populateRelatedView(tv.getText().toString());
                    RecipeDataType recipeDataType = TAGS;
                    RecipeDataStore.getsInstance(getActivity()).getRecipeList(
                            recipeDataType, new RecipeDataStoreListenerImpl(), singletag);
                }
            });
        }

        ImageView clearTag = (ImageView) rootView.findViewById(R.id.cleartag);
        final HorizontalScrollView scrollView = (HorizontalScrollView)rootView.findViewById(R.id.list_scroll);
        scrollView.postDelayed(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            }
        }, 100L);

        clearTag.setColorFilter(getResources().getColor(R.color.black));
        clearTag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
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
