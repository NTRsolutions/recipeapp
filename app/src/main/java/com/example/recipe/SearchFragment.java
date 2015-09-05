package com.example.recipe;


import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {
    SearchView mSearchView;
    ListView mListView;
    ArrayAdapter  mAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchView = (SearchView) root.findViewById(R.id.search_view);
        mListView = (ListView) root.findViewById(R.id.list_view);

        String[] values = new String[] { "Vanilla", "Chocochip", "chocolate",
                "blackcurrent", "strawberry","JellyBean", "fudge"};

        final ArrayList<String> list = new ArrayList(Arrays.asList(values));

        final CharSequence text = "Hello toast!";

        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                list);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT).show();
            }
        });
        setupSearchView();
        return root;
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        return false;
    }

    private void setUpListView() {

    }


    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mAdapter.getFilter().filter("");
        } else {
            mAdapter.getFilter().filter(newText.toString());
        }
        return true;
    }
}
