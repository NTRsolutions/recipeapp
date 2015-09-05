package com.example.recipe.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.data.CategoryDataStore;
import com.example.recipe.R;

import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CatgoryViewHolder> {

    public interface AdapterListener{
        String onAdapterClickListener(String s);
    }

    private List<CategoryDataStore.CategoryDataItem> dataItems;

    private Context context;

    final private AdapterListener adapterListener;

    public CategoryAdapter(Context context, List<CategoryDataStore.CategoryDataItem> dataItems, AdapterListener listener){
        this.context = context;
        this.dataItems =  dataItems;
        this.adapterListener = listener;
    }

    @Override
    public CatgoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.layout_card_item,viewGroup, false);
        CatgoryViewHolder mh = new CatgoryViewHolder(v, new ClickResolver(adapterListener));
        return mh;

    }

    @Override
    public void onBindViewHolder(CatgoryViewHolder myViewHolder, int i) {

        CategoryDataStore.CategoryDataItem dataItem = dataItems.get(i);
        myViewHolder.mTitle.setText(dataItem.getItem());
        myViewHolder.mDescription.setText(dataItem.getItemDescription());
        myViewHolder.mIcon.setImageResource(dataItem.getImageResource());
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public static class ClickResolver implements CatgoryViewHolder.ViewHolderListener {
        AdapterListener mListener;

        public ClickResolver( AdapterListener listener){
            mListener = listener;
        }

        @Override
        public String onViewHolderClicked(String s) {
            if(mListener != null) {
                mListener.onAdapterClickListener(s);
                Log.d("TAG", "in adapter click" + s);
            }
            return s;
        }
    }



}
