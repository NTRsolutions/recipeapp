package com.example.recipe.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class FavouriteFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter MyAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<CategoryDataStore.CategoryDataItem> cupCakes;

    public FavouriteFragment(){}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivityListener) {
            MainActivityListener dataProvider = (MainActivityListener) activity;
            cupCakes = dataProvider.getData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container,
                false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        CategoryAdapter mAdapter = new CategoryAdapter(getActivity(),cupCakes, new AdapterClickResolver());
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }


}
