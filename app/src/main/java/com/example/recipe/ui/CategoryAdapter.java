package com.example.recipe.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.data.Category;
import com.example.recipe.data.CategoryDataStore;
import com.example.recipe.R;
import com.example.recipe.utility.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CatgoryViewHolder> {

    public interface CategoryAdapterListener {
        void onCategoryAdapterListener();
    }

    private List<Category> mDataItems = new ArrayList<>();
    private Context mContext;
    private CategoryAdapterListener mCategoryAdapterListener;

    public CategoryAdapter(Context context, CategoryAdapterListener categoryAdapterListener){
        this.mContext = context;
        this.mCategoryAdapterListener = categoryAdapterListener;
        CategoryDataStore.fetchAllCategoryData(mContext, new CategoryDataStoreListenerImpl());
    }

    private class CategoryDataStoreListenerImpl implements
            CategoryDataStore.CategoryDataStoreListener {

        @Override
        public void onDataFetchComplete(List<Category> list) {
            mDataItems = list;
            notifyDataSetChanged();
        }
    }

    @Override
    public CatgoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.layout_card_item,viewGroup, false);
        CatgoryViewHolder mh = new CatgoryViewHolder(v, new ClickResolver(this));
        return mh;

    }

    @Override
    public void onBindViewHolder(CatgoryViewHolder myViewHolder, int i) {
        Category dataItem = mDataItems.get(i);
        myViewHolder.onBind(dataItem);
    }

    @Override
    public int getItemCount() {
        return mDataItems.size();
    }

    public static class ClickResolver implements CatgoryViewHolder.CategoryViewHolderListener {
        CategoryAdapter adapter;

        ClickResolver(CategoryAdapter categoryAdapter) {
            adapter = categoryAdapter;
        }

        @Override
        public String onViewHolderClicked(String s) {
            if (adapter != null && adapter.mCategoryAdapterListener != null) {
                adapter.mCategoryAdapterListener.onCategoryAdapterListener();
            }
            return null;
        }
    }



}
