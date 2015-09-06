package com.example.recipe.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.R;
import com.example.recipe.data.Category;
import com.example.recipe.data.CategoryDataStore;
import com.example.recipe.utility.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class FavourateAdapter extends RecyclerView.Adapter<CatgoryViewHolder> {
    private List<Category> dataItems = new ArrayList<>();

    private Context context;

    final private BaseFragment.AdapterListener adapterListener;

    public FavourateAdapter(Context context, BaseFragment.AdapterListener listener){
        this.context = context;
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

    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public static class ClickResolver implements CatgoryViewHolder.ViewHolderListener {
        BaseFragment.AdapterListener mListener;

        public ClickResolver( BaseFragment.AdapterListener listener){
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
