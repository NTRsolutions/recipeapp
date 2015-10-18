package com.example.recipe;


import android.app.Activity;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;

import android.support.v4.util.Pair;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.recipe.R;
import com.example.recipe.data.RecipeDataStore;
import com.example.recipe.data.RecipeInfo;
import com.example.recipe.utility.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    SearchView mSearchView;
    ListView mListView;
    ArrayAdapter  mAdapter;
    Handler mHandler;
    int UPDATE_TIME = 1000;
    String mLastQuery;
    List<RecipeInfo> mInfoList;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchView = (SearchView) root.findViewById(R.id.search_view);
        mListView = (ListView) root.findViewById(R.id.list_view);

        final ArrayList<String> list = new ArrayList(Arrays.asList());
        final CharSequence text = "Hello toast!";

        mAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                list);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RecipeInfo info = mInfoList.get(i);
                if (getActivity() instanceof MainActivity) {
                    Utility.hideKeyboard(mSearchView);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.showDetailView(Integer.parseInt(info.getDocId()));
                }
            }
        });

        setupSearchView();
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, UPDATE_TIME);

        return root;
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSearchView != null) {
                mHandler.postDelayed(mRunnable, UPDATE_TIME);
            }

            String query = mSearchView.getQuery().toString();
            if (query == null || query.isEmpty() || query.length() <= 2) {
                return;
            }

            if (!query.equalsIgnoreCase(mLastQuery)) {
                mLastQuery = query;
                mInfoList = RecipeDataStore.getsInstance(getActivity()).
                        searchDocumentBasedOnTitle(query, 100);
                List<String> titleList = getTitles(mInfoList);
                mAdapter.clear();
                mAdapter.addAll(titleList);
                mAdapter.notifyDataSetChanged();
            }
        }
    };


    List<String> getTitles(List<RecipeInfo> infoList) {
        ArrayList<String> retList = new ArrayList<>(infoList.size());
        for (RecipeInfo info : infoList) {
            retList.add(info.getTitle());
        }

        return retList;
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint("Search Here");
        Utility.showKeyboard(mSearchView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }
}
